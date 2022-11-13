import * as vscode from 'vscode';

export class TreeItem extends vscode.TreeItem {
	children: TreeItem[]|undefined;
  
    constructor(label: string, iconPath: string, children?: TreeItem[] ) {
	  super(
		  label,
		  children === undefined ? vscode.TreeItemCollapsibleState.None :
								   vscode.TreeItemCollapsibleState.Expanded);
	  this.children = children;
      this.iconPath = new vscode.ThemeIcon(iconPath);
      this.command = {
        "title": "Reload",
        "command": "dagserver-vs.loadView",
		"arguments": [[this.label,iconPath]]
     };
	}
    
  }