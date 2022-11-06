/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	var __webpack_modules__ = ([
/* 0 */,
/* 1 */
/***/ ((module) => {

module.exports = require("vscode");

/***/ }),
/* 2 */
/***/ ((__unused_webpack_module, exports, __webpack_require__) => {


Object.defineProperty(exports, "__esModule", ({ value: true }));
exports.DagserverAuthenticationProvider = exports.AUTH_TYPE = void 0;
const vscode_1 = __webpack_require__(1);
const vscode = __webpack_require__(1);
exports.AUTH_TYPE = `dagserver`;
const AUTH_NAME = `dagserver`;
class DagserverAuthenticationProvider {
    constructor(context) {
        this.context = context;
        this._sessionChangeEmitter = new vscode_1.EventEmitter();
        this._disposable = vscode_1.Disposable.from(vscode_1.authentication.registerAuthenticationProvider(exports.AUTH_TYPE, AUTH_NAME, this, { supportsMultipleAccounts: false }));
    }
    get onDidChangeSessions() {
        return this._sessionChangeEmitter.event;
    }
    async getSessions(scopes) {
        const allSessions = await this.context.secrets.get("dagserver");
        if (allSessions) {
            return JSON.parse(allSessions);
        }
        return [];
    }
    async createSession(scopes) {
        try {
            let username = vscode.workspace.getConfiguration().get("username");
            let pwd = vscode.workspace.getConfiguration().get("password");
            const token = "token";
            // eslint-disable-next-line curly
            if (!token)
                throw new Error(`Dagserver login failure`);
            const session = {
                id: "qqqq-wwwww-eeeee-rrrrr",
                accessToken: token,
                account: {
                    label: "labrluser",
                    id: "testid"
                },
                scopes: []
            };
            await this.context.secrets.store("dagserver", JSON.stringify([session]));
            this._sessionChangeEmitter.fire({ added: [session], removed: [], changed: [] });
            return session;
        }
        catch (e) {
            console.log(e);
            vscode.window.showErrorMessage(`Sign in failed: ${e}`);
            throw e;
        }
    }
    async removeSession(sessionId) {
        const allSessions = await this.context.secrets.get("dagserver");
        if (allSessions) {
            let sessions = JSON.parse(allSessions);
            const sessionIdx = sessions.findIndex(s => s.id === sessionId);
            const session = sessions[sessionIdx];
            sessions.splice(sessionIdx, 1);
            await this.context.secrets.store("dagserver", JSON.stringify(sessions));
            if (session) {
                this._sessionChangeEmitter.fire({ added: [], removed: [session], changed: [] });
            }
        }
    }
    async dispose() {
        this._disposable.dispose();
    }
}
exports.DagserverAuthenticationProvider = DagserverAuthenticationProvider;


/***/ })
/******/ 	]);
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be isolated against other modules in the chunk.
(() => {
var exports = __webpack_exports__;

Object.defineProperty(exports, "__esModule", ({ value: true }));
exports.deactivate = exports.activate = void 0;
// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
const vscode = __webpack_require__(1);
const authentication_provider_1 = __webpack_require__(2);
// This method is called when your extension is activated
// Your extension is activated the very first time the command is executed
function activate(context) {
    // Use the console to output diagnostic information (console.log) and errors (console.error)
    // This line of code will only be executed once when your extension is activated
    console.log('Congratulations, your extension "dagserver-vs" is now active!');
    // The command has been defined in the package.json file
    // Now provide the implementation of the command with registerCommand
    // The commandId parameter must match the command field in package.json
    let disposable = vscode.commands.registerCommand('dagserver-vs.helloWorld', () => {
        // The code you place here will be executed every time your command is executed
        // Display a message box to the user
        vscode.window.showInformationMessage('Hello World from dagserver-vs!');
    });
    context.subscriptions.push(disposable);
    context.subscriptions.push(new authentication_provider_1.DagserverAuthenticationProvider(context));
    getDagserverSession();
    context.subscriptions.push(vscode.authentication.onDidChangeSessions(async (e) => {
        getDagserverSession();
    }));
}
exports.activate = activate;
const getDagserverSession = async () => {
    const session = await vscode.authentication.getSession("dagserver", [], { createIfNone: false });
    if (session) {
        vscode.window.showInformationMessage(`Logged to dagserver as ${session.account.label}`);
    }
};
// This method is called when your extension is deactivated
function deactivate() { }
exports.deactivate = deactivate;

})();

module.exports = __webpack_exports__;
/******/ })()
;
//# sourceMappingURL=extension.js.map