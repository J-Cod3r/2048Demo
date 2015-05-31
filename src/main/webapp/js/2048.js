/**
 * Code by J.Cod3r Date: 2014-08-16
 */
var board = new Array(); // 用于表示每个棋盘格
var score = 0; // 用于表示分数
var hascollided = new Array(); // 用于表示单元格是否合并
var documentWidth = window.screen.availWidth; // 屏幕宽度
var containerWidth = 0.92 * documentWidth; // 最外层div宽度
var cellWidth = 0.18 * documentWidth; // 每个单元格宽度
var cellSpace = 0.04 * documentWidth; // 每个单元格的间距

var startx = 0;
var starty = 0;
var endx = 0;
var endy = 0;

$(function() {
	prepare4Moble();
	newgame();
});

function prepare4Moble() {
	if (documentWidth > 500) {
		containerWidth = 400;
		cellWidth = 87.5;
		cellSpace = 10;
	}
	$("#container").css({
		"width" : containerWidth - 2*cellSpace,
		"height" : containerWidth - 2*cellSpace,
		"padding" : cellSpace,
		"border-radius" : 0.02*containerWidth
	});
	$(".cell").css({
		"width" : cellWidth,
		"height" : cellWidth,
		"border-radius" : 0.02*cellWidth
	});
}

$(document).keydown(function(event){
    switch(event.keyCode){
        case 37: //left
        	event.preventDefault();
            if(moveLeft()){
                setTimeout("generateNumber()", 210);
                setTimeout("isgameover()", 300);
            }
            break;
        case 38: //up
        	event.preventDefault();
            if(moveUp()){
                setTimeout("generateNumber()", 210);
                setTimeout("isgameover()", 300);
            }
            break;
        case 39: //right
        	event.preventDefault();
            if(moveRight()){
                setTimeout("generateNumber()", 210);
                setTimeout("isgameover()", 300);
            }
            break;
        case 40: //down
        	event.preventDefault();
            if(moveDown()){
                setTimeout("generateNumber()", 210);
                setTimeout("isgameover()", 300);
            }
            break;
        default: //default
            break;
    }
});


document.addEventListener('touchstart', function(event){
	startx = event.touches[0].pageX;
	starty = event.touches[0].pageY;
});

document.addEventListener('touchmove', function(event){
	event.preventDefault();
});

document.addEventListener('touchend', function(event){
	endx = event.changedTouches[0].pageX;
	endy = event.changedTouches[0].pageY;
	
	var deltax = endx - startx;
	var deltay = endy - starty;
	
	if(Math.abs(deltax) < 0.3*documentWidth && Math.abs(deltay) < 0.3*documentWidth)
		return;
	
	//x轴进行滑动
	if(Math.abs(deltax) >= Math.abs(deltay)){
		if(deltax > 0){
			//move right
			if(moveRight()){
				setTimeout("generateNumber()", 210);
				setTimeout("isgameover()", 300);
			}
		}else{
			//move left	
			if(moveLeft()){
				setTimeout("generateNumber()", 210);
				setTimeout("isgameover()", 300);
			}
			
		}
	}
	//y轴进行滑动
	else{
		if(deltay > 0){
			//move down
			if(moveDown()){
				setTimeout("generateNumber()", 210);
				setTimeout("isgameover()", 300);
			}
		}else{
			//move up
			if(moveUp()){
				setTimeout("generateNumber()", 210);
				setTimeout("isgameover()", 300);
			}
		}	
	}
});

function isgameover() {
	if (nospace() && nomove()) {
		gameover();
	}
}

function nomove() {
	if (canMoveLeft() || canMoveRight() || canMoveUp() || canMoveDown()) {
		return false;
	}
	return true;
}

function gameover() {
	alert("游戏结束！");
}

function newgame() {
	// 初始化棋盘格
	initCell();
	// 在随机的2个单元格中生成数字
	generateNumber();
	generateNumber();
	$("#score").text(0);
}

function initCell() {
	// 设置每个小单元格的位置
	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			$("#cell-" + i + "-" + j).css({
				"top" : getPosTop(i),
				"left" : getPosLeft(j)
			});
		}
	}

	for (var i = 0; i < 4; i++) {
		board[i] = new Array();
		hascollided[i] = new Array();
		for (var j = 0; j < 4; j++) {
			board[i][j] = 0;
			hascollided[i][j] = false;
		}
	}

	updateCellView();
	
	score = 0;
}

function updateCellView() {
	$(".number-cell").remove();
	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			$("#container").append(
					'<div id="number-cell-' + i + '-' + j
							+ '" class="number-cell"></div>');
			var numberCell = $('#number-cell-' + i + '-' + j);
			if (board[i][j] == 0) {
				numberCell.css({
					"width" : "0px",
					"height" : "0px",
					"top" : (getPosTop(i) + cellWidth/2),
					"left" : (getPosLeft(j) + cellWidth/2)
				});
			} else {
				numberCell.css({
					"width" : cellWidth,
					"height" : cellWidth,
					"top" : getPosTop(i),
					"left" : getPosLeft(j),
					"color" : getNumberColor(board[i][j]),
					"background-color" : getNumberBackgroundColor(board[i][j])
				});
				numberCell.text(board[i][j]);
			}
			hascollided[i][j] = false;
		}
	}
	$(".number-cell").css("line-height", cellWidth + "px");
	$(".number-cell").css("font-size", 0.6*cellWidth + "px");
}

// 生成一个随机数
function generateNumber() {
	// 判断是否有空闲的单元格
	if (nospace()) {
		return false;
	}
    //随机一个位置
    var randx = parseInt(Math.floor( Math.random()*4));
    var randy = parseInt(Math.floor( Math.random()*4));
	var count = 0;
	var temporary=new Array();
	for(var i=0;i<4;i++) {
		for(var j=0;j<4;j++){
			if(board[i][j]==0) {
				temporary[count] = i*4+j;
				count++;
			}
		}
	}
	var pos = parseInt(Math.floor(Math.random()*count));
	randx = Math.floor(temporary[pos]/4);
	randy = Math.floor(temporary[pos]%4);
    //随机一个数字
    var randNumber = Math.random() < 0.5 ? 2 : 4;
    //在随机位置显示随机数字
    board[randx][randy] = randNumber;
    showNumberWithAnimation(randx, randy, randNumber);
	return true;
}

// 显示数字的动画效果
function showNumberWithAnimation(i, j, randNumber){
	var numberCell = $('#number-cell-' + i + "-" + j);
	numberCell.css({
		"color" : getNumberColor(randNumber),
		"background-color" : getNumberBackgroundColor(randNumber)
	});
	numberCell.text(randNumber);
	numberCell.animate({
		width : cellWidth,
		height : cellWidth,
		top : getPosTop(i),
		left : getPosLeft(j)	
	}, 50);
}

// 查看是否有空闲的单元格
function nospace() {
	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			if (board[i][j] == 0) {
				return false;
			}
		}
	}
	return true;
}

//设置单元格背景色
function getNumberBackgroundColor(number) {
	switch (number) {
		case 2: return "#eee4da"; break;
		case 4: return "#ede0c8"; break;
		case 8: return "#f2b179"; break;
		case 16: return "#f59563"; break;
		case 32: return "#f07c5f"; break;
		case 64: return "#ff5e3b"; break;
		case 128: return "#edcf72"; break;
		case 256: return "#fd0361"; break;
		case 512: return "#9c0"; break;
		case 1024: return "#33b5e5"; break;
		case 2048: return "#09c"; break;
		case 4096: return "#a6c"; break;
		case 8192: return "#93c"; break;
		case 16384: return "#888"; break;
		default: return "#111"; break;
	}
}

// 设置单元格前景色，即：字体颜色
function getNumberColor(number) {
	if (number <= 4) {
		return "#776e65";
	}
	return "white";
}

function getPosTop(y) {
	return cellSpace + y * (cellSpace + cellWidth);
}

function getPosLeft(x) {
	return cellSpace + x * (cellSpace + cellWidth);
}

function moveLeft(){
    if(!canMoveLeft())
        return false;
    for (var i = 0; i < 4; i++) {
		for (var j = 1; j < 4; j++) {
			if (board[i][j] != 0) {
				for (var k = 0; k < j; k++) {
					if (board[i][k] == 0 && noDisorderX(i, k, j)) {
						showMove(i, j, i, k);
						board[i][k] = board[i][j];
						board[i][j] = 0;
						continue;
					} else if (board[i][k] == board[i][j] && noDisorderX(i, k, j) && !hascollided[i][k]) {
						showMove(i, j, i, k);
						board[i][k] += board[i][j];
						board[i][j] = 0;
						score += board[i][k];
						updateScore();
						hascollided[i][k] = true;
						continue;
					}
				}
			}
		}
	}
    setTimeout(updateCellView(), 200);
    return true;
}

function moveRight(){
	if(!canMoveRight())
		return false;
	for (var i = 0; i < 4; i++) {
		for (var j = 2; j >= 0; j--) {
			if (board[i][j] != 0) {
				for (var k = 3; k > j; k--) {
					if (board[i][k] == 0 && noDisorderX(i, k, j)) {
						showMove(i, j, i, k);
						board[i][k] = board[i][j];
						board[i][j] = 0;
						continue;
					} else if (board[i][k] == board[i][j] && noDisorderX(i, k, j) && !hascollided[i][k]) {
						showMove(i, j, i, k);
						board[i][k] += board[i][j];
						board[i][j] = 0;
						score += board[i][k];
						updateScore();
						hascollided[i][k] = true;
						continue;
					}
				}
			}
		}
	}
	setTimeout(updateCellView(), 200);
	return true;
}

function moveUp(){
	if(!canMoveUp())
		return false;
	for (var j = 0; j < 4; j++) {
		for (var i = 1; i < 4; i++) {
			if (board[i][j] != 0) {
				for (var k =0; k < i; k++) {
					if (board[k][j] == 0 && noDisorderY(j, k, i)) {
						showMove(i, j, k, j);
						board[k][j] = board[i][j];
						board[i][j] = 0;
						continue;
					} else if (board[k][j] == board[i][j] && noDisorderY(j, k, i) && !hascollided[k][j]) {
						showMove(i, j, k, j);
						board[k][j] += board[i][j];
						board[i][j] = 0;
						score += board[k][j];
						updateScore();
						hascollided[k][j] = true;
						continue;
					}
				}
			}
		}
	}
	setTimeout(updateCellView(), 200);
	return true;
}
function moveDown(){
	if(!canMoveDown())
		return false;
	for (var j = 0; j < 4; j++) {
		for (var i = 2; i >= 0; i--) {
			if (board[i][j] != 0) {
				for (var k = 3; k > i; k--) {
					if (board[k][j] == 0 && noDisorderY(j, i, k)) {
						showMove(i, j, k, j);
						board[k][j] = board[i][j];
						board[i][j] = 0;
						continue;
					} else if (board[k][j] == board[i][j] && noDisorderY(j, i, k) && !hascollided[k][j]) {
						showMove(i, j, k, j);
						board[k][j] += board[i][j];
						board[i][j] = 0;
						score += board[k][j];
						updateScore();
						hascollided[k][j] = true;
						continue;
					}
				}
			}
		}
	}
	setTimeout(updateCellView(), 200);
	return true;
}

function showMove(fromx, fromy, tox, toy) {
	$('#number-cell-' + fromx + "-" + fromy).animate({
		top : getPosTop(tox),
		left : getPosLeft(toy)
	}, 200);
}

function noDisorderX(row, col1, col2) {
	for (var i = col1 + 1; i < col2; i++) {
		if (board[row][i] != 0) {
			return false;
		}
	}
	return true;
}

function noDisorderY(col, row1, row2) {
	for (var i = row1 + 1; i < row2; i++) {
		if (board[i][col] != 0) {
			return false;
		}
	}
	return true;
}

function canMoveLeft() {
	for (var i = 0; i < 4; i++) {
		for (var j = 0; j < 4; j++) {
			if (board[i][j] != 0) {
				if (board[i][j - 1] == 0 || board[i][j - 1] == board[i][j]) {
					return true;
				}
			}
		}
	}
	return false;
}

function canMoveRight() {
	for (var i = 0; i < 4; i++) {
		for (var j = 2; j >= 0; j--) {
			if (board[i][j] != 0) {
				if (board[i][j + 1] == 0 || board[i][j + 1] == board[i][j]) {
					return true;
				}
			}
		}
	}
	return false;
}

function canMoveUp() {
	for (var j = 0; j < 4; j++) {
		for (var i = 1; i < 4; i++) {
			if (board[i][j] != 0) {
				if (board[i - 1][j] == 0 || board[i - 1][j] == board[i][j]) {
					return true;
				}
			}
		}
	}
	return false;
}

function canMoveDown() {
	for (var j = 0; j < 4; j++) {
		for (var i = 2; i > 0; i--) {
			if (board[i][j] != 0) {
				if (board[i + 1][j] == 0 || board[i + 1][j] == board[i][j]) {
					return true;
				}
			}
		}
	}
	return false;
}

function updateScore() {
	$("#score").text(score);
}