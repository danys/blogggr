#!/usr/bin/env node
var ncp = require('ncp');
var options = {};
options['filter'] = new RegExp('dist$|dist/css$|dist/fonts.*$|dist/js$|dist/.*min\.css$|dist/.*min\.js$','m');
ncp('./node_modules/bootstrap/dist/', './vendor/bootstrap/', options, function (err) {
 if (err) {
   return console.error(err);
 }
 console.log('Done copying Bootstap!');
});

options['filter'] = new RegExp('dist$|dist/.*min\.js$','m');
ncp('./node_modules/jquery/dist/', './vendor/jquery/', options, function (err) {
  if (err) {
    return console.error(err);
  }
  console.log('Done copying jquery!');
});
