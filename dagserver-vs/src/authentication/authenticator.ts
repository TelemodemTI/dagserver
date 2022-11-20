
import * as WebSocket from 'ws';

export class Authenticator{

    host:string;
    private  _socket: WebSocket | null = null;

    constructor(host:string){
        this.host = host;
        //`ws://localhost:3002`
        this._socket = new WebSocket(this.host+"/vscode");
    }

    public async login(username:string,pwd:string) : Promise<string>{
        return new Promise((resolve,reject)=>{
            let message = {
                type: "login",
                args: [username,pwd]
            };
            this._socket?.send(JSON.stringify(message));
            this._socket?.on('message', (data: any) => {
                let msg = data.toString();
                resolve(msg);
            });
        });
    }

}