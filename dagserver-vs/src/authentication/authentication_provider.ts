import { authentication, AuthenticationProvider, AuthenticationProviderAuthenticationSessionsChangeEvent, AuthenticationSession, Disposable, EventEmitter, ExtensionContext, ProgressLocation } from "vscode";
import * as vscode from 'vscode';
import { Authenticator } from "./authenticator";


export const AUTH_TYPE = `dagserver`;
const AUTH_NAME = `dagserver`;

export class DagserverAuthenticationProvider implements AuthenticationProvider, Disposable {
    private _sessionChangeEmitter = new EventEmitter<AuthenticationProviderAuthenticationSessionsChangeEvent>();
    private _disposable: Disposable;
    private _authenticator: Authenticator;
  
    constructor(private readonly context: ExtensionContext) {
        this._disposable = Disposable.from(authentication.registerAuthenticationProvider(AUTH_TYPE, AUTH_NAME, this, { supportsMultipleAccounts: false }))
        let host = vscode.workspace.getConfiguration().get<string>("host")!;
        this._authenticator = new Authenticator(host);
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
          let username = vscode.workspace.getConfiguration().get<string>("username")!;
          let pwd = vscode.workspace.getConfiguration().get<string>("password")!;
          
          const token = await this._authenticator.login(username,pwd);
          //const token = "asdasdad";
          // eslint-disable-next-line curly
          if (!token) throw new Error(`Dagserver login failure`);
          const session: AuthenticationSession = {
            id: username,
            accessToken: token,
            account: {
              label: username,
              id: username
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

}

