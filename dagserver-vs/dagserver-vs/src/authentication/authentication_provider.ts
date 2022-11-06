import { authentication, AuthenticationProvider, AuthenticationProviderAuthenticationSessionsChangeEvent, AuthenticationSession, Disposable, EventEmitter, ExtensionContext, ProgressLocation } from "vscode";
import * as vscode from 'vscode';

export const AUTH_TYPE = `dagserver`;
const AUTH_NAME = `dagserver`;

export class DagserverAuthenticationProvider implements AuthenticationProvider, Disposable {
    private _uriHandler = new UriEventHandler();
    private _sessionChangeEmitter = new EventEmitter<AuthenticationProviderAuthenticationSessionsChangeEvent>();
    private _disposable: Disposable;
    
  
    constructor(private readonly context: ExtensionContext) {
        this._disposable = Disposable.from(authentication.registerAuthenticationProvider(AUTH_TYPE, AUTH_NAME, this, { supportsMultipleAccounts: false }))
    }

    get onDidChangeSessions() {
        return this._sessionChangeEmitter.event;
    }

    public async getSessions(scopes?: string[]): Promise<readonly AuthenticationSession[]> {
        const allSessions = await this.context.secrets.get("dagserver");

        if (allSessions) {
        return JSON.parse(allSessions) as AuthenticationSession[];
        }

        return [];
    }

    public async createSession(scopes: string[]): Promise<AuthenticationSession> {
        try {
          const uri = vscode.Uri.parse(`http://localhost:3000/`);
          await vscode.env.openExternal(uri);


          let codeExchangePromise = promiseFromEvent(this._uriHandler.event, this.handleUri(scopes));
          

          const token = await Promise.race([
            codeExchangePromise.promise,
          ]);
          
          // eslint-disable-next-line curly
          if (!token) throw new Error(`Dagserver login failure`);
          const session: AuthenticationSession = {
            id: "qqqq-wwwww-eeeee-rrrrr",
            accessToken: token,
            account: {
              label: "labrluser",
              id: "testid"
            },
            scopes: []
          };
          await this.context.secrets.store("dagserver", JSON.stringify([session]))
          this._sessionChangeEmitter.fire({ added: [session], removed: [], changed: [] });
               return session;
        } catch (e) {    
          console.log(e);
          vscode.window.showErrorMessage(`Sign in failed: ${e}`);
          throw e;
        }
          
    }

    public async removeSession(sessionId: string): Promise<void> {
        const allSessions = await this.context.secrets.get("dagserver");
        if (allSessions) {
        let sessions = JSON.parse(allSessions) as AuthenticationSession[];
        const sessionIdx = sessions.findIndex(s => s.id === sessionId);
        const session = sessions[sessionIdx];
        sessions.splice(sessionIdx, 1);

        await this.context.secrets.store("dagserver", JSON.stringify(sessions));

        if (session) {
            this._sessionChangeEmitter.fire({ added: [], removed: [session], changed: [] });
        }      
        }
    }

    public async dispose() {
        this._disposable.dispose();
    }


    private handleUri: (scopes: readonly string[]) => PromiseAdapter<vscode.Uri, string> = 
    (scopes) => async (uri, resolve, reject) => {
      const query = new URLSearchParams(uri.fragment);
      const accessToken = query.get('access_token');
      
  
      if (!accessToken) {
        reject(new Error('No token'));
        return;
      }
      resolve(accessToken);
    };

}

class UriEventHandler extends EventEmitter<vscode.Uri> implements vscode.UriHandler {
  public handleUri(uri: vscode.Uri) {
    this.fire(uri);
  }
}



export interface PromiseAdapter<T, U> {
	(
		value: T,
		resolve:
			(value: U | PromiseLike<U>) => void,
		reject:
			(reason: any) => void
	): any;
}

const passthrough = (value: any, resolve: (value?: any) => void) => resolve(value);

export function promiseFromEvent<T, U>(event: vscode.Event<T>, adapter: PromiseAdapter<T, U> = passthrough): { promise: Promise<U>; cancel: EventEmitter<void> } {
	let subscription: Disposable;
	let cancel = new EventEmitter<void>();
  

  event((value: T) => {
    try {
      console.log(value)
    } catch (error) {
      console.log(error)
    }
  });


	return {
		promise: new Promise<U>((resolve, reject) => {
			cancel.event(_ => reject('Cancelled'));
      subscription = event((value: T) => {
				try {
					Promise.resolve(adapter(value, resolve, reject))
						.catch(reject);
				} catch (error) {
					reject(error);
				}
			});
		}).then(
			(result: U) => {
				subscription.dispose();
				return result;
			},
			error => {
				subscription.dispose();
				throw error;
			}
		),
		cancel
	};
}