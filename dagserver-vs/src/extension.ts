// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import { DagserverAuthenticationProvider } from './authentication/authentication_provider';
import { DagDetailView } from './dag/dag_detail_view';


import { DagExplorer } from './explorer/dag_explorer';

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed

export function activate(context: vscode.ExtensionContext) {

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
					console.log(message)
				});

			});
		} else {
			vscode.window.showInformationMessage('DAG is not scheduled!');	
		}
	});
	context.subscriptions.push(disposable1);

	let disposable2 = vscode.commands.registerCommand('dagserver-vs.schedule', () => {
		vscode.window.showInformationMessage('scheduled');
	});
	context.subscriptions.push(disposable2);

	let disposable3 = vscode.commands.registerCommand('dagserver-vs.unschedule', () => {
		vscode.window.showInformationMessage('unschedule');
	});
	context.subscriptions.push(disposable3);

	
	
	getDagserverSession(context);

	context.subscriptions.push(
		vscode.authentication.onDidChangeSessions(async e => {
			getDagserverSession(context);
		})
	);

}




const getDagserverSession = async (context: vscode.ExtensionContext) => {
	const session = await vscode.authentication.getSession("dagserver", [], { createIfNone: false });
	if (session) {
		let explorer : DagExplorer = new DagExplorer(context);
		vscode.window.registerTreeDataProvider('explorer',explorer );
		await explorer.refresh();	
		vscode.window.showInformationMessage(`Logged to dagserver as ${session.account.label}`);
	}
};

// This method is called when your extension is deactivated
export function deactivate() {}
