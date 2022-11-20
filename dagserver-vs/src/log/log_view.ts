import * as vscode from 'vscode';
import WebSocket = require('ws');

export class LogViewer {

    private  _socket: WebSocket | null = null;

    constructor(private readonly context: vscode.ExtensionContext){
        let host = vscode.workspace.getConfiguration().get<string>("host")!;
        this._socket = new WebSocket(host+"/vscode");
    }

    async getLogData(logid:any){
        let sessionstr :any = await this.context.secrets.get("dagserver"); 
        let session = JSON.parse(sessionstr)[0];
        if(session && session.accessToken){
            return new Promise((resolve,reject)=>{
                let token = session.accessToken;
                let message = {
                    type: "log",
                    args: [token,logid]
                };
                this._socket?.send(JSON.stringify(message));
                this._socket?.on('message', (data: any) => {
                    resolve(data);
                });
            });
        } else {
            return Promise.resolve();
        }
    }
}