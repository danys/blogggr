#!/usr/bin/env node
var sass = require('node-sass');
var fs = require('fs');
var cssDir = './dist/css/';
var cssOutputFile = cssDir+'style.css';
var scssMapOutputFile = cssDir+'style.css.map';
sass.render({
  file: './scss/style.scss',
  outFile: cssOutputFile,
  sourceMap: true,
  outputStyle: 'expanded'
}, function(error, result) {
if (error) {
    console.log(error.status); // used to be "code" in v2x and below 
    console.log(error.column);
    console.log(error.message);
    console.log(error.line);
  }
  else {

    console.log('Compilation success');
    fs.writeFile(cssOutputFile, result.css, function(err){
    	if(!err){
    		console.log('Wrote file!');
    	}
    	else console.log('Error writing output file!');
    });
    fs.writeFile(scssMapOutputFile, result.map, function(err){
    	if(!err){
    		console.log('Wrote map file!');
    	}
    	else console.log('Error writing scss map output file!');
    });
  }
});