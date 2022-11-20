// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import * as path from 'path';
import { DagserverAuthenticationProvider } from './authentication/authentication_provider';
import { DagDetailView } from './dag/dag_detail_view';


import { DagExplorer } from './explorer/dag_explorer';
import { LogViewer } from './log/log_view';

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed

export function activate(context: vscode.ExtensionContext) {

	let explorer : DagExplorer = new DagExplorer(context);
	context.subscriptions.push(new DagserverAuthenticationProvider(context));
	let disposable1 = vscode.commands.registerCommand('dagserver-vs.loadView', (args:any) => {
		if(args[1] !== "terminal"){
			let view = new DagDetailView(context,args);
			view.configure().then((logs:any)=>{
				const panel = vscode.window.createWebviewPanel(DagDetailView.viewType,"Log Executions",vscode.ViewColumn.One,{
					enableScripts: true
				  });
				panel.webview.html = view.getHtmlForWebview(logs);
				panel.webview.onDidReceiveMessage(message => {
					vscode.commands.executeCommand('dagserver-vs.loadLog',message.text);
				});

			});
		} else {
			vscode.window.showInformationMessage('DAG is not scheduled!');	
		}
	});
	context.subscriptions.push(disposable1);

	

	let disposable4 = vscode.commands.registerCommand('dagserver-vs.loadLog', (args:any) => {
		let log = new LogViewer(context);
		log.getLogData(args).then((data:any)=>{
			vscode.workspace.openTextDocument({
				content: data.toString(), 
				language: "text"
			});
			vscode.window.showInformationMessage('load log file');
		});
	});
	context.subscriptions.push(disposable4);


	let disposable5 = vscode.commands.registerCommand('dagserver-vs.reloadExplorer', (args:any) => {
		getDagserverSession(context,explorer);
	});
	context.subscriptions.push(disposable5);
	
	
	getDagserverSession(context,explorer);

	context.subscriptions.push(
		vscode.authentication.onDidChangeSessions(async e => {
			getDagserverSession(context,explorer);
		})
	);

}




const getDagserverSession = async (context: vscode.ExtensionContext,explorer: DagExplorer) => {
	const session = await vscode.authentication.getSession("dagserver", [], { createIfNone: false });
	if (session) {
		
		vscode.window.registerTreeDataProvider('explorer',explorer );
		await explorer.refresh();	
		vscode.window.showInformationMessage(`Logged to dagserver as ${session.account.label}`);
	} else {
		await explorer.refresh();
	}
};

// This method is called when your extension is deactivated
export function deactivate() {}
