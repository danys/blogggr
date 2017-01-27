#!/usr/bin/env node
var mini = require('clean-css');
var fs = require('fs');
var srcFile = './dist/css/style.css';
var destinationFile = './dist/css/style.min.css';
var source = fs.readFileSync(srcFile,"UTF-8");
var output = new mini({ compatibility: 'ie8' , sourceMap: false}).minify(source);
//console.log(output);
fs.writeFile(destinationFile, output.styles, function(err){
    	if(!err){
    		console.log('Wrote minified CSS file!');
    	}
    	else console.log('Error writing minified CSS file!');
    });
