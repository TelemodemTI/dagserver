// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import { DagserverAuthenticationProvider } from './authentication/authentication_provider';

// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
export async function activate(context: vscode.ExtensionContext) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "dagserver-vs" is now active!');

	context.subscriptions.push(new DagserverAuthenticationProvider(context));

	
	getDagserverSession();


	context.subscriptions.push(
		vscode.authentication.onDidChangeSessions(async e => {
			getDagserverSession();
		})
	);

	

	

}

const getDagserverSession = async () => {
	const session = await vscode.authentication.getSession("dagserver", [], { createIfNone: false });
	if (session) {
		vscode.window.showInformationMessage(`Logged to dagserver as ${session.account.label}`);
	}
};

// This method is called when your extension is deactivated
export function deactivate() {}
