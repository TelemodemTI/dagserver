import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { BehaviorSubject } from 'rxjs';
import { GraphQLOutputPort } from 'src/app/application/outputs/graphql.output.port';
import { Agents } from 'src/app/domain/models/agent.model';
import { Credential } from 'src/app/domain/models/credential.model';
import { AvailableJobs } from 'src/app/domain/models/availableJobs.model';
import { Uncompileds } from 'src/app/domain/models/uncompiled.model';
import { Scheduled } from 'src/app/domain/models/scheduled.modem';
import { ExecuteResult } from 'src/app/domain/models/executeResult.model';
import { Log } from 'src/app/domain/models/log.model';
import { Property } from 'src/app/domain/models/property.model';


@Injectable({
  providedIn: 'root'
})
export class GraphQLOutputPortAdapterService implements GraphQLOutputPort {

  constructor(private apollo : Apollo) { }

  getEntries(): Promise<any[]> {
    return new Promise<any[]>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "query keystoreEntries($token: String) {keystoreEntries(token:$token) {name,type}}"
      this.query(string,{token:token}).subscribe((result:any)=>{
        if(result && result.keystoreEntries){
          resolve(result.keystoreEntries as any[]);
        }
      })
    })
  }
  createKeyEntry(alias: any,key:any,pwd:any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation createKeyEntry($token:String,$alias:String,$key:String,$pwd:String) { createKeyEntry(token:$token,alias:$alias,key:$key,pwd:$pwd) {status,code,value} }"
      this.query(string,{token:token,alias:alias,key:key,pwd:pwd}).subscribe((result:any)=>{
        if(result && result.createKeyEntry && result.createKeyEntry.status == "ok"){
              resolve();
        }
      })
    })
  }
  removeEntry(alias: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation removeEntry($token:String,$alias:String) { removeEntry(token:$token,alias:$alias) {status,code,value} }"
      this.query(string,{token:token,alias:alias}).subscribe((result:any)=>{
        if(result && result.removeEntry && result.removeEntry.status == "ok"){
              resolve();
        }
      })

    })
  }


  deleteApiKey(appname: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation deleteApiKey($token:String,$appname:String) { deleteApiKey(token:$token,appname:$appname) {status,code,value} }"
      this.query(string,{token:token,appname:appname}).subscribe((result:any)=>{
        if(result && result.deleteApiKey && result.deleteApiKey.status == "ok"){
              resolve();
        }
      })

    })
  }
  
  createApiKey(appname: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation createApiKey($token:String,$appname:String) { createApiKey(token:$token,appname:$appname) {status,code,value} }"
      this.query(string,{token:token,appname:appname}).subscribe((result:any)=>{
        if(result && result.createApiKey && result.createApiKey.status == "ok"){
              resolve();
        }
      })

    })
  }
  
  createCopy(filename: string, filename_copy: string): Promise<any> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation copyFile($token:String,$filename:String,$copyname:String) { copyFile(token:$token,filename:$filename,copyname:$copyname) {status,code,value} }"
      this.query(string,{token:token,filename:filename,copyname:filename_copy}).subscribe((result:any)=>{
        if(result && result.copyFile && result.copyFile.status == "ok"){
              resolve();
        }
      })
    });
  }
  moveFile(folder:string,filename: string, newpath: string): Promise<any> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation moveFile($token:String,$folder:String,$filename:String,$newpath:String) { moveFile(token:$token,folder:$folder,filename:$filename,newpath:$newpath) {status,code,value} }"
      this.query(string,{token:token,folder:folder,filename:filename,newpath:newpath}).subscribe((result:any)=>{
        if(result && result.moveFile && result.moveFile.status == "ok"){
              resolve();
        }
      })
    });
  }
  delete(selected_folder: string, selected_file: string): Promise<any> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation deleteFile($token:String,$folder:String,$file:String) { deleteFile(token:$token,folder:$folder,file:$file) {status,code,value} }"
      this.query(string,{token:token,folder:selected_folder,file:selected_file}).subscribe((result:any)=>{
        if(result && result.deleteFile && result.deleteFile.status == "ok"){
              resolve();
        }
      })
    });
  }
  
  createFolder(folder: any): Promise<any> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "mutation createFolder($token:String,$foldername:String) { createFolder(token:$token,foldername:$foldername) {status,code,value} }"
      this.query(string,{token:token,foldername:folder}).subscribe((result:any)=>{
        if(result && result.createFolder && result.createFolder.status == "ok"){
              resolve();
            }
      })
    });
  }
  
  mounted(): Promise<any> {
    return new Promise<any>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_refresh_token")
      var string = "query mounted($token: String) {mounted(token:$token) { name ,type, content { name , type, content { name,type  }} }}"
      this.query(string,{token:token}).subscribe((result:any)=>{
        if(result && result.mounted){
          resolve(result.mounted as any[]);
        }
      })
    })
  }

  logout(): Promise<void> {
	return new Promise<void>((resolve,reject)=>{
		var token = localStorage.getItem("dagserver_refresh_token")
		var string = "mutation logout($token:String) { logout(token:$token) {status,code,value} }"
		this.query(string,{token:token}).subscribe((result:any)=>{
			if(result && result.logout && result.logout.status == "ok"){
	          resolve();
	        }
		})
	});
  }

  reimport(jarname: any): Promise<any> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation reimport($token:String,$jarname:String) { reimport(token:$token,jarname:$jarname) {status,code,value} }"
      this.query(string,{token:token,jarname:jarname}).subscribe((result:any)=>{
        if(result && result.reimport){
          resolve(result.reimport);
        }
      })
    })
  }

  
  removeException(eventDt: string): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation removeException($token:String,$eventDt:String) { removeException(token:$token,eventDt:$eventDt) {status,code,value} }"
      this.query(string,{token:token,eventDt:eventDt}).subscribe((result:any)=>{
        if(result && result.removeException && result.removeException.status == "ok"){
          resolve()
        } else if(result && result.removeException) {
          reject(result.removeException.status)
        } 
      })
    })
  }
  
  getExceptions(): Promise<any[]> {
    return new Promise<any[]>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "query exceptions($token: String) {exceptions(token:$token) {eventDt,classname,method,stack,evalkey}}"
      this.query(string,{token:token}).subscribe((result:any)=>{
        if(result && result.exceptions){
          resolve(result.exceptions as any[]);
        }
      })
    })
  }


  getExceptionsFromExecution(evalkey:String): Promise<any[]> {
    return new Promise<any[]>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "query exceptionsFromExecution($token: String, $evalkey:String) {exceptionsFromExecution(token:$token, evalkey:$evalkey) {eventDt,classname,method,stack,evalkey}}"
      this.query(string,{token:token, evalkey:evalkey}).subscribe((result:any)=>{
        if(result && result.exceptionsFromExecution){
          resolve(result.exceptionsFromExecution as any[]);
        }
      })
    })
  }
  

  getLastLogs(): Promise<Log[]> {
    return new Promise<Log[]>((resolve, reject) => {
      var string = "query last {last {id,dagname,execDt,value,outputxcom,status, channel,marks , evalkey}}"
      this.query(string,{}).subscribe((result:any)=>{
        if(result && result.last){
          resolve(result.last as Log[]);
        }
      })
    })
  }
  
  
  renameUncompiled(uncompiled: any, arg1: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation renameUncompiled($token:String,$uncompiled:Int,$newname:String) { renameUncompiled(token:$token,uncompiled:$uncompiled,newname:$newname) {status,code,value} }"
      this.query(string,{token:token,uncompiled:uncompiled,newname:arg1}).subscribe((result:any)=>{
        if(result && result.renameUncompiled && result.renameUncompiled.status == "ok"){
          resolve()
        } else if(result && result.renameUncompiled) {
          reject(result.renameUncompiled.status)
        }      
      })
    })
  }
  removeLog(id: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteLog($token:String,$logid:Int) { deleteLog(token:$token,logid:$logid) {status,code,value} }"
      this.query(string,{token:token,logid:id}).subscribe((result:any)=>{
        if(result && result.deleteLog && result.deleteLog.status == "ok"){
          resolve()
        } else if(result && result.deleteLog) {
          reject(result.deleteLog.status)
        }      
      })
    })
  }
  removeAllLog(dagname: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteAllLogs($token:String,$dagname:String) { deleteAllLogs(token:$token,dagname:$dagname) {status,code,value} }"
      this.query(string,{token:token,dagname:dagname}).subscribe((result:any)=>{
        if(result && result.deleteAllLogs && result.deleteAllLogs.status == "ok"){
          resolve()
        } else if(result && result.deleteAllLogs) {
          reject(result.deleteAllLogs.status)
        }      
      })
    })
  }
  exportUncompiled(uncompiledId: number): Promise<any> {
    return new Promise<any>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "query exportUncompiled($token:String,$uncompiled:Int) { exportUncompiled(token:$token,uncompiled:$uncompiled) }"
      this.query(string,{token:token,uncompiled:uncompiledId}).subscribe((result:any)=>{
        if(result && result.exportUncompiled){
          resolve(result.exportUncompiled)
        } else if(result && result.exportUncompiled) {
          reject(result.exportUncompiled.status)
        }      
      })
    })
  }
  
  

  removeJarfile(jarname: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteJarfile($token:String,$jarname:String) { deleteJarfile(token:$token,jarname:$jarname) { status, code, value } }"
      this.query(string,{token:token,jarname:jarname}).subscribe((result:any)=>{
        if(result && result.deleteJarfile){
          resolve(result.deleteJarfile)
        } 
      })
      resolve();
    })
  }

  updateProp(group:String, name: String, value: String): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation updateProp($token:String,$group:String,$key:String,$value:String) { updateProp(token:$token,group:$group,key:$key,value:$value) { status, code, value } }"
      this.query(string,{token:token,group:group,key:name,value:value}).subscribe((result:any)=>{
        if(result && result.updateProp){
          resolve(result.getDependencies)
        } 
      })
      resolve();
    })
  }
  
  
  getDependencies(jarname: string, dagname: string): Promise<any[]> {
    return new Promise<any>((resolve,reject)=>{
      var string = "query getDependencies($jarname:String,$dagname:String) {   getDependencies(jarname:$jarname,dagname:$dagname) {     onStart,onEnd   } }"
      this.query(string,{jarname:jarname,dagname:dagname}).subscribe((result:any)=>{
        if(result && result.getDependencies){
          resolve(result.getDependencies)
        } 
      })
    })
  }
  

  getIcons(type: string): Promise<string> {
    return new Promise<string>((resolve,reject)=>{
      var string = "query getIcons($type:String) { getIcons(type:$type)  }"
      this.query(string,{type:type}).subscribe((result:any)=>{
        if(result && result.getIcons){
          resolve(result.getIcons)
        } 
      })
    })
  }

  deleteAccount(username: any): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteAccount($token:String,$username:String) { deleteAccount(token:$token,username:$username) { status, code, value }  }"
      this.query(string,{token:token,username:username}).subscribe((result:any)=>{
        if(result && result.deleteAccount && result.deleteAccount.status == "ok"){
          resolve()
        } else if(result && result.deleteAccount) {
          reject(result.deleteAccount.status)
        }
      })
    })
  }


  createAccount(useracc: string, type: string, pwdHash: string): Promise<void> {
    return new Promise<void>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation createAccount($token:String,$username:String,$accountType:String, $pwdHash:String) { createAccount(token:$token,username:$username,accountType:$accountType,pwdHash:$pwdHash) { status, code, value }  }"
      this.query(string,{token:token,username:useracc,accountType:type,pwdHash:pwdHash}).subscribe((result:any)=>{
        console.log(result)
        if(result && result.createAccount && result.createAccount.status == "ok"){
          resolve()
        } else if(result && result.createAccount) {
          reject(result.createAccount.status)
        }
      })
    })
  }


  getCredentials(): Promise<Credential[]> {
    return new Promise<Credential[]>((resolve,reject)=>{
      var token = localStorage.getItem("dagserver_token")
      var string = "query credentials($token:String) { credentials(token:$token) { id,username,typeAccount }  }"
      this.query(string,{token:token}).subscribe((result:any)=>{
        console.log(result)
        if(result && result.credentials){
          resolve(result.credentials)
        } else if(result && result.credentials) {
          reject(result.credentials.status)
        }
      })
    })
  }

  deleteGroupProperty(name: any, group: any): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteGroupProperty($token:String, $name: String, $group: String) { deleteGroupProperty(token:$token,name:$name,group:$group) { status,code,value } }"
      this.query(string,{token:token,name:name, group:group}).subscribe((result:any)=>{
        if(result.deleteGroupProperty && result.deleteGroupProperty.status == "ok"){
          resolve()
        } else if(result && result.deleteGroupProperty) {
          reject(result.deleteGroupProperty.status)
        }
      })
    })
  }

  deleteUncompiled(uncompiledId: number): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteUncompiled($token:String, $uncompiled:Int){deleteUncompiled(token:$token,uncompiled:$uncompiled){ status,code,value }}"
      this.query(string,{token:token,uncompiled:uncompiledId}).subscribe((result:any)=>{
        if(result && result.deleteUncompiled && result.deleteUncompiled.status == "ok"){
            resolve();
        }
      })
    })
  }
  
  compile(uncompiledId: number): Promise<String> {
    return new Promise<String>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation compile($token:String, $uncompiled:Int){compile(token:$token,uncompiled:$uncompiled){ status,code,value }}"
      this.query(string,{token:token,uncompiled:uncompiledId}).subscribe((result:any)=>{
        if(result && result.compile && result.compile.status == "ok"){
          resolve(result.compile.value)
        } else if(result && result.compile && result.compile.status) {
          reject({status:result.compile.status,message:result.compile.value})
        }
      })
    })
  }
  
  saveUncompiled(uncompiledId: number, base64: String): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation updateUncompiled($token:String, $uncompiled:Int , $bin:String){updateUncompiled(token:$token,uncompiled:$uncompiled,bin:$bin){ status,code,value }}"
      this.query(string,{token:token,uncompiled:uncompiledId,bin:base64}).subscribe((result:any)=>{
        if(result && result.updateUncompiled && result.updateUncompiled.status == "ok"){
          resolve()
        } else {
          if(result && result.updateUncompiled){
            reject(result.updateUncompiled.status)
          }
        }
      })
    })
  }

  createProperty(name: String, description: String, value: String, group: String): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation createProperty($token:String, $name: String, $description: String, $value: String , $group: String) { createProperty(token:$token,name:$name,description:$description,value:$value,group:$group) { status,code,value } }"
      this.query(string,{token:token,name:name,description:description, value:value , group:group}).subscribe((result:any)=>{
        if(result.createProperty && result.createProperty.status == "ok"){
          resolve()
        } else {
          reject(result.createProperty.status)
        }
      })
    })
  }
  properties(): Promise<Property[]> {
    return new Promise<Property[]>((resolve, reject) => {
      var string = "query properties { properties { name,group,description,value }}"
      this.query(string,{}).subscribe((result:any)=>{
        if(result && result.properties){
          resolve(result.properties)
        }
      })
    })
  }
  deleteProperty(name: String, group: String): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation deleteProperty($token:String, $name: String, $group: String) { deleteProperty(token:$token,name:$name,group:$group) { status,code,value } }"
      this.query(string,{token:token,name:name, group:group}).subscribe((result:any)=>{
        if(result.deleteProperty && result.deleteProperty.status == "ok"){
          resolve()
        } else {
          reject(result.deleteProperty.status)
        }
      })
    })
  }
  logs(dagname: String): Promise<Log[]> {
    return new Promise<Log[]>((resolve, reject) => {
      var string = "query logs($dagname:String!) {logs(dagname:$dagname) {id,dagname,execDt,value,outputxcom,xcomkey,status, channel,marks , evalkey}}"
      this.query(string,{dagname:dagname}).subscribe((result:any)=>{
        console.log(result.logs)
        if(result && result.logs){
          resolve(result.logs as Log[]);
        }
      })
    })
  }
  createUncompiled(bin: String): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation saveUncompiled($token:String!, $bin:String) {saveUncompiled(token:$token,bin:$bin) {status,code,value}}";
      this.query(string,{token:token,bin:bin}).subscribe((result:any)=>{   
        if(result && result.saveUncompiled && result.saveUncompiled.status == "ok"){
          resolve()
        } else {
          if(result && result.saveUncompiled)
          reject(result.saveUncompiled.value)
        }
      })
    })
  }
  getDetail(jarname: String): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      let string = "query detail($jarname: String!) { detail(jarname: $jarname){ status , detail { dagname,cronExpr,group,onStart,onEnd,node {index,operations}}}}";
      this.query(string,{jarname:jarname}).subscribe((result:any)=>{
        if(result && result.detail && result.detail.status == "ok"){
          resolve(result)
        } 
      })
    })
  }
  executeDag(dagname: String, jarname: String,data:String): Promise<ExecuteResult> {
    return new Promise<ExecuteResult>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation executeDag($token: String!,$dagname:String!, $jarname: String!, $data: String!) {executeDag(token:$token,dagname:$dagname,jarname:$jarname,data:$data) {status,code,value}}";
      this.query(string,{dagname:dagname,jarname:jarname,token:token,data:data}).subscribe((result:any)=>{
        if(result && result.executeDag && result.executeDag.status){
          let title_msje = (result.executeDag.status == "ok")?"WARNING":"ERROR"
          let error_msje = result.executeDag.value
          resolve({title_msje:title_msje,error_msje:error_msje} as ExecuteResult)
        }
      })
    })
    
  }
  unscheduleDag(dagname: String, jarname: String): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")!
      var string = "mutation unscheduleDag($token: String!,$dagname:String!, $jarname: String!) {unscheduleDag(token:$token,dagname:$dagname,jarname:$jarname) {status,code,value}}";
      this.query(string,{dagname:dagname,jarname:jarname,token:token}).subscribe((result:any)=>{
        if(result && result["unscheduleDag"]){
          if(result["unscheduleDag"].status == "ok"){
            resolve()
          } else {
            reject()
          }
        }
      })
    })
  }
  scheduleDag(dagname: String, jarname: String): Promise<void>  {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")!
      var string = "mutation scheduleDag($token: String!,$dagname:String!, $jarname: String!) {scheduleDag(token:$token,dagname:$dagname,jarname:$jarname) {status,code,value}}";
      this.query(string,{dagname:dagname,jarname:jarname,token:token}).subscribe((result:any)=>{
        if(result && result["scheduleDag"]){
          if(result["scheduleDag"].status == "ok"){
            resolve()
          } else {
            console.log(result["scheduleDag"].value)
            reject(result["scheduleDag"].value)
          }
        }
      })
    })
  }
  getAvailableJobs(): Promise<AvailableJobs[]> {
    return new Promise<AvailableJobs[]>((resolve, reject) => {
      var string = "query availableJobs {availableJobs {jarname,classname,groupname,dagname,cronExpr,triggerEvent,targetDagname,owner}}"
      this.query(string,{}).subscribe((result:any)=>{
        if(result && result.availableJobs){
          resolve(result.availableJobs as AvailableJobs[])
        }
      })
    })
  }
  getUncompileds(): Promise<Uncompileds[]> {
    return new Promise<Uncompileds[]>((resolve, reject) => {
      var uncomp = "query getUncompileds($token:String) { getUncompileds(token:$token){ uncompiledId,bin,createdDt }}"
      var token = localStorage.getItem("dagserver_token")!
      this.query(uncomp,{token:token}).subscribe((result:any)=>{
        if(result && result.getUncompileds){
          let rv = result.getUncompileds.map((item:any)=>{ 
            if(item){
              item.decoded = JSON.parse(item.bin) ; 
              return item; 
            }
          }) as Uncompileds[]
          resolve(rv)
        }
      })
    })
    
  }
  getScheduledJobs(): Promise<Scheduled[]> {
    return new Promise<Scheduled[]>((resolve, reject) => {
      var string = "query scheduledJobs { scheduledJobs { dagname,groupname,eventTrigger,nextFireAt} }"
      this.query(string,{}).subscribe((result:any)=>{
        if(result && result.scheduledJobs){
          resolve(result.scheduledJobs as Scheduled[])
        }
      })
    })
  }
  login(ureqobject: any): Promise<boolean> {
    var token = btoa(JSON.stringify(ureqobject));
    return new Promise<boolean>((resolve, reject) => {
      const string = "query login($token: String!) { login(token:$token) {token,refreshToken} }";
      this.query(string,{token:token}).subscribe((result:any)=>{
        try {
          if(result){
            if (result.login ) {
              let token = result.login.token
              let refresh_token = result.login.refreshToken
              if(token && refresh_token){
                localStorage.setItem("dagserver_token", token);
                localStorage.setItem("dagserver_refresh_token", refresh_token);
                resolve(true);
              } else {
                resolve(false);
              }
            } else {
              resolve(false);
            }  
          }
        } catch (error) {
          console.log(error)
          resolve(false) 
        }
      });
    });
  }


  operatorsMetadata(): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const string = "query operatorsMetadata { operatorsMetadata }";
      this.query(string,{}).subscribe((result:any)=>{
        console.log(result)
        if (result && result.operatorsMetadata) {
          resolve(result.operatorsMetadata);
        } 
      }, (error: any) => {
        reject(error);
      });
    });
  }




  agents(): Promise<Agents[]> {
    return new Promise<Agents[]>((resolve, reject) => {
      var string = "query agents { agents { id , name, hostname, updatedOn } }"
      this.query(string,{}).subscribe((result:any)=>{
        if(result && result.agents){
          resolve(result.agents as Agents[])
        }
      })
    })
  }
  


  query(querystring: string,params : any){
    let response = new BehaviorSubject(undefined);
    let subscrition = this.apollo.query({
      query: gql(querystring),
      variables: params,
      errorPolicy: 'all',
      fetchPolicy: 'no-cache',
    }).subscribe((result:any)=>{
        
        subscrition.unsubscribe()
        if(result && result.data)
          response.next(result.data)
    })
    return response
  }
  updateParamsCompiled(jarname: string, idope: string,typeope: string, bin: any): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")!
      var string = "mutation updateParamsCompiled($token:String, $idope:String,, $typeope:String, $jarname:String, $bin:String) { updateParamsCompiled(token:$token, idope:$idope,typeope:$typeope, jarname:$jarname, bin:$bin) { code, status, value } }"
      this.query(string,{token:token,idope:idope,typeope:typeope,jarname:jarname,bin:bin}).subscribe((result:any)=>{
        if(result && result.updateParamsCompiled){
          resolve()
        }
      })
    })
  }
}


