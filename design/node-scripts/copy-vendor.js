#!/usr/bin/env node
var ncp = require('ncp');
var options = {};

//Copy Bootstrap
options['filter'] = new RegExp('dist$|dist/css$|dist/fonts.*$|dist/js$|dist/.*min\.css$|dist/.*min\.js$','m');
ncp('./node_modules/bootstrap/dist/', './vendor/bootstrap/', options, function (err) {
 if (err) {
   return console.error(err);
 }
 console.log('Done copying Bootstap!');
});

//Copy jquery
options['filter'] = new RegExp('dist$|dist/.*min\.js$','m');
ncp('./node_modules/jquery/dist/', './vendor/jquery/', options, function (err) {
  if (err) {
    return console.error(err);
  }
  console.log('Done copying jquery!');
});

//Copy font-awesome
options['filter'] = new RegExp('font-awesome$|/css$|fonts$|css/.*min\.css$|fonts/.*$','m');
ncp('./node_modules/font-awesome/', './vendor/font-awesome/', options, function (err) {
  if (err) {
    return console.error(err);
  }
  console.log('Done copying font-awesome!');
});
