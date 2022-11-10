// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import { DagserverAuthenticationProvider } from './authentication/authentication_provider';
import { DagExplorer } from './explorer/dag_explorer';

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed

export function activate(context: vscode.ExtensionContext) {

	context.subscriptions.push(new DagserverAuthenticationProvider(context));

	
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
		console.log("ypaso o mas")
		let explorer : DagExplorer = new DagExplorer(context);
		vscode.window.registerTreeDataProvider('explorer',explorer );
		await explorer.refresh();
		vscode.window.showInformationMessage(`Logged to dagserver as ${session.account.label}`);
	}
};

// This method is called when your extension is deactivated
export function deactivate() {}
