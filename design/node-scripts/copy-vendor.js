#!/usr/bin/env node
var ncp = require('ncp');
var options = {};
options['filter']='/.*\.css/g';
ncp('./node_modules/bootstrap/dist/', './vendor/bootstrap/', options, function (err) {
 if (err) {
   return console.error(err);
 }
 console.log('done!');
});