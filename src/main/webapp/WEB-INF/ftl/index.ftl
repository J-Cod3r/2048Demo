<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>2048</title>
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <script src="https://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="js/2048.js"></script>
    <link rel="stylesheet" type="text/css" href="css/2048.css" />
  </head>
  <body>
    <div class="header">
    	<h1>2048</h1>
    	<a href="javascript:newgame();" id="gameButton">New Game</a>
    	<p>score：<span id="score">0</span>分</p>
    </div>
    <div id="container">
    	<div id="cell-0-0" class="cell"></div>
    	<div id="cell-0-1" class="cell"></div>
    	<div id="cell-0-2" class="cell"></div>
    	<div id="cell-0-3" class="cell"></div>
    	
    	<div id="cell-1-0" class="cell"></div>
    	<div id="cell-1-1" class="cell"></div>
    	<div id="cell-1-2" class="cell"></div>
    	<div id="cell-1-3" class="cell"></div>
    	
    	<div id="cell-2-0" class="cell"></div>
    	<div id="cell-2-1" class="cell"></div>
    	<div id="cell-2-2" class="cell"></div>
    	<div id="cell-2-3" class="cell"></div>
    	
    	<div id="cell-3-0" class="cell"></div>
    	<div id="cell-3-1" class="cell"></div>
    	<div id="cell-3-2" class="cell"></div>
    	<div id="cell-3-3" class="cell"></div>
    </div>
    <div class="welcome">
    	${"欢迎您，来自${country}-${city}的网友！有BUG，凑合玩吧，#^_^#。"}
    </div>
  </body>
</html>
