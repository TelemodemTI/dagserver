const express = require('express')
const app = express()

app.use(express.static('public'));

const port = 3000

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.get('/token', (req, res) => {
    //res.send('tokenized')
    res.redirect('vscode://dagserver-vs.dagserver-vs?access_token=tokenized');
  })

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})
