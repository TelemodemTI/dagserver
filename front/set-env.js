const { writeFile } = require('fs');

// read environment variables from .env file
require('dotenv').config();

// read the command line arguments passed with yargs


const targetPath1 = `./src/assets/defaults.js`; 
// we have access to our environment variables
// in the process.env object thanks to dotenv
const environmentFileContent = `
var environment = {
   dagserverUri : "${process.env.DAGSERVERURI}"
};
`;

// write the content to the respective file
writeFile(targetPath1, environmentFileContent, function (err) {
   if (err) {
      console.log(err);
   }

   console.log(`Wrote variables to ${targetPath1}`);
});

