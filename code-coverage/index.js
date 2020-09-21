const express = require('express');
const app = express();

app.use(express.static('result'))

app.listen(5000);