#!/usr/bin/env node
var fs = require('fs');
var uglifyJS = require('uglify-js');
var result = uglifyJS.minify("./js/main.js");
destinationFile = './dist/js/main.min.js';
fs.writeFile(destinationFile, result.code, function(err){
    	if(!err){
    		console.log('Wrote minified JS file!');
    	}
    	else console.log('Error writing minified JS file!');
    });