const { writeFile } = require('fs');

// read environment variables from .env file
require('dotenv').config();

// read the command line arguments passed with yargs


const targetPaths = [`./src/assets/defaults.js`,`../WebContent/WEB-INF/cli/assets/defaults.js`]; 



// we have access to our environment variables
// in the process.env object thanks to dotenv
const environmentFileContent = `
var environment = {
   dagserverUri : "${process.env.DAGSERVERURI}"
};
`;

let promarr = []
for (let index = 0; index < targetPaths.length; index++) {
   promarr.push(new Promise((resolve,reject)=>{
      const targetPath1 = targetPaths[index];
      // write the content to the respective file
      writeFile(targetPath1, environmentFileContent, function (err) {
         if (err) {
            reject(err);
         }
         console.log(environmentFileContent)
         console.log(`Wrote variables to ${targetPath1}`);
         resolve(true);
      });
   }))
}

Promise.all(promarr,(returned)=>{
   console.log("final")
   console.log(returned)
})

