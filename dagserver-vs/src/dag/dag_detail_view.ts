import * as vscode from 'vscode';
import { ExtensionContext } from 'vscode';
import WebSocket = require('ws');


export class DagDetailView {

    private  _socket: WebSocket | null = null;

	public static readonly viewType = 'Dagserver.DagDetailView';

	constructor(private readonly context: ExtensionContext,private readonly args:any) { 
        let host = vscode.workspace.getConfiguration().get<string>("host")!;
        this._socket = new WebSocket(host+"/vscode");

    }

    public async configure(){
        let sessionstr :any = await this.context.secrets.get("dagserver"); 
        return new Promise ((resolve,reject)=>{
            let session = JSON.parse(sessionstr)[0];
            if(session && session.accessToken){
                    let token = session.accessToken;
                    let message = {
                        type: "logs",
                        args: [token,this.args[0]]
                    };
                    this._socket?.send(JSON.stringify(message));
                    this._socket?.on('message', (data: any) => {
                        let msg = JSON.parse(data.toString());
                        this._socket?.close();
                        resolve(msg);
                    });
            }
        });
    }

	public getHtmlForWebview(logs:any) {
        let tablebody = "";
        
        for (let index = 0; index < logs.length; index++) {
            const log = logs[index];
            tablebody = tablebody + `<tr>
                <td>${log.id}</td>
                <td>${log.dagname}</td>
                <td>${log.execdt}</td>
                <td>
                    <button onclick="handleHowdyClick(${log.id})">View</button>
                </td>  
            </tr>`;
        }
		return `<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>Cat Colors</title>
			</head>
			<body>
        
        <script>
                const vscode = acquireVsCodeApi();
                
                function handleHowdyClick(id) {
                    vscode.postMessage({
                      command: "logDetails",
                      text: id,
                    });
                }
    
        </script>

            <table border=1>
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Dagname</th>
                        <th>ExecutedAt</th>
                        <th>View</th>  
                    </tr>
                </thead>
                <tbody>
                    ${tablebody}
                </tbody>
            </table>


			</body>
			</html>`;
	}
}