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
        if(result.updateUncompiled && result.updateUncompiled.status == "ok"){
          resolve()
        } else {
          reject(result.updateUncompiled.status)
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
      var string = "query logs($dagname:String!) {logs(dagname:$dagname) {id,dagname,execDt,value,xcomoutput,status}}"
      this.query(string,{dagname:dagname}).subscribe((result:any)=>{
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
  executeDag(dagname: String, jarname: String): Promise<ExecuteResult> {
    return new Promise<ExecuteResult>((resolve, reject) => {
      var token = localStorage.getItem("dagserver_token")
      var string = "mutation executeDag($token: String!,$dagname:String!, $jarname: String!) {executeDag(token:$token,dagname:$dagname,jarname:$jarname) {status,code,value}}";
      this.query(string,{dagname:dagname,jarname:jarname,token:token}).subscribe((result:any)=>{
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
            reject()
          }
        }
      })
    })
  }
  getAvailableJobs(): Promise<AvailableJobs[]> {
    return new Promise<AvailableJobs[]>((resolve, reject) => {
      var string = "query availableJobs {availableJobs {jarname,classname,groupname,dagname,cronExpr,triggerEvent,targetDagname}}"
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
          resolve(result.getUncompileds.map((item:any)=>{ item.decoded = JSON.parse(item.bin) ; return item; }) as Uncompileds[])
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
  login(user: any, pwd: any): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      const string = "query login($username: String!,$pwd: String!) { login(username:$username,pwdhash:$pwd) }";
      this.query(string,{username:user,pwd: pwd}).subscribe((result:any)=>{
      
        console.log(result)
        if (result && result.login) {
          localStorage.setItem("dagserver_token", result.login);
          console.log("test")
          resolve(true);
        } 
      }, (error: any) => {
        reject(error);
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

}

