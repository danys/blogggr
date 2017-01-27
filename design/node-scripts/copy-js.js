#!/usr/bin/env node
var fs = require('fs');
fs.createReadStream('./js/main.js').pipe(fs.createWriteStream('./dist/js/main.js'));