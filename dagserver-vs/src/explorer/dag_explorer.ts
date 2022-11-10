import * as vscode from 'vscode';
import { ExtensionContext } from 'vscode';
import WebSocket = require('ws');
import { TreeItem } from './tree_item';

export class DagExplorer implements vscode.TreeDataProvider<TreeItem> {
    
    private  _socket: WebSocket | null = null;
    private _onDidChangeTreeData: vscode.EventEmitter<TreeItem | null> = new vscode.EventEmitter<TreeItem | null>();
    readonly onDidChangeTreeData: vscode.Event<TreeItem | null> = this._onDidChangeTreeData.event;


    data: TreeItem[];
  
	constructor(private readonly context: ExtensionContext) {
      let host = vscode.workspace.getConfiguration().get<string>("host")!;
      console.log(host)
      this._socket = new WebSocket(host+"/vscode");
	  this.data = [];
	}

    getTreeItem(element: TreeItem): vscode.TreeItem|Thenable<vscode.TreeItem> {
        return element;
      }
    
    getChildren(element?: TreeItem|undefined): vscode.ProviderResult<TreeItem[]> {
        if (element === undefined) {
          return this.data;
        }
        return element.children;
    }
    async refresh() {
        let sessionstr :any = await this.context.secrets.get("dagserver"); 
        let session = JSON.parse(sessionstr)[0];
        if(session && session.accessToken){
            return new Promise((resolve,reject)=>{
                let token = session.accessToken;
                let message = {
                    type: "availables",
                    args: [token]
                };
                console.log(message)
                this._socket?.send(JSON.stringify(message));
                this._socket?.on('message', (data: any) => {
                    let msg = data.toString();
                    console.log(msg);
                    let datao:any = JSON.parse(msg);
                    let keys1 = Object(datao);
                    for (let index = 0; index < keys1.length; index++) {
                        const key = keys1[index];
                        this.data.push(new TreeItem(key, "combine"));
                    }
                    this._onDidChangeTreeData.fire(null);
                    resolve(true);
                });
            })
        } else {
            return Promise.resolve()
        }
        
    }
    
}