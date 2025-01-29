
// Setting up canvas
const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");

const gridSize = 10; // 10x10 board
const cellSize = 40; // Each cell is 40px
canvas.width = gridSize * cellSize;
canvas.height = gridSize * cellSize;

// Game board (2D array filled with 0s)
let board = Array(gridSize).fill(null).map(() => Array(gridSize).fill(0));
let score = 0;

// Block shapes
const blockShapes = [
    [[1, 1], [1, 1]],       // Square
    [[1, 1, 1, 1]],         // Line (horizontal)
    [[1], [1], [1], [1]],   // Line (vertical)
    [[1, 1, 0], [0, 1, 1]], // Z-shape
    [[0, 1, 1], [1, 1, 0]], // Reverse Z-shape
    [[1, 1, 1], [0, 1, 0]], // T-shape
    [[1, 1], [1, 0]],       // L-shape
    [[1, 1], [0, 1]]        // Reverse L-shape
];

let currentBlock = getRandomBlock(); // Holds the currently dragged block
let blockPos = { x: 3, y: 0 }; // Position of the block

// Generate a random block
function getRandomBlock() {
    return blockShapes[Math.floor(Math.random() * blockShapes.length)];
}
function checkFullRows() {
    let rowsCleared = 0;

    for (let row = 0; row < gridSize; row++) {
        if (board[row].every(cell => cell === 1)) {
            board.splice(row, 1);
            board.unshift(Array(gridSize).fill(0)); // Add empty row on top
            rowsCleared++;
        }
    }

    if (rowsCleared > 0) {
        score += rowsCleared * 100; // Each row = 100 points
        updateScoreDisplay();
    }
}

// Draw the game board
function drawBoard() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    for (let row = 0; row < gridSize; row++) {
        for (let col = 0; col < gridSize; col++) {
            if (board[row][col] === 1) {
                ctx.fillStyle = "blue";
                ctx.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                ctx.strokeRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }
}
function updateScoreDisplay() {
    document.getElementById("score").innerText = score;
}
function checkGameOver() {
    for (let row = 0; row < gridSize; row++) {
        for (let col = 0; col < gridSize; col++) {
            for (let shape of blockShapes) {
                if (canPlaceBlock(shape, col, row)) {
                    return false; // At least one block can fit
                }
            }
        }
    }

    alert("Game Over! Final Score: " + score);
    location.reload(); // Restart the game
    return true;
}


// Draw the current block
function drawBlock(block, x, y) {
    ctx.fillStyle = "red";
    for (let row = 0; row < block.length; row++) {
        for (let col = 0; col < block[row].length; col++) {
            if (block[row][col] === 1) {
                ctx.fillRect((x + col) * cellSize, (y + row) * cellSize, cellSize, cellSize);
                ctx.strokeRect((x + col) * cellSize, (y + row) * cellSize, cellSize, cellSize);
            }
        }
    }
}

// Check if block can be placed
function canPlaceBlock(block, x, y) {
    for (let row = 0; row < block.length; row++) {
        for (let col = 0; col < block[row].length; col++) {
            if (block[row][col] === 1) {
                let boardX = x + col;
                let boardY = y + row;

                if (boardX < 0 || boardX >= gridSize || boardY >= gridSize || board[boardY][boardX] === 1) {
                    return false;
                }
            }
        }
    }
    return true;
}

// Place block on the board
function placeBlock() {
    if (!canPlaceBlock(currentBlock, blockPos.x, blockPos.y)) return;
    
    for (let row = 0; row < currentBlock.length; row++) {
        for (let col = 0; col < currentBlock[row].length; col++) {
            if (currentBlock[row][col] === 1) {
                board[blockPos.y + row][blockPos.x + col] = 1;
            }
        }
    }

    checkFullRows();
    currentBlock = getRandomBlock();
    blockPos = { x: 3, y: 0 };
}

// Check for full rows and clear them
function checkFullRows() {
    for (let row = 0; row < gridSize; row++) {
        if (board[row].every(cell => cell === 1)) {
            board.splice(row, 1);
            board.unshift(Array(gridSize).fill(0)); // Add new empty row on top
        }
    }
}

// Drag and drop logic
let dragging = false;
canvas.addEventListener("mousedown", (e) => {
    dragging = true;
});
canvas.addEventListener("mousemove", (e) => {
    if (dragging) {
        let rect = canvas.getBoundingClientRect();
        let mouseX = e.clientX - rect.left;
        let mouseY = e.clientY - rect.top;
        
        blockPos.x = Math.floor(mouseX / cellSize);
        blockPos.y = Math.floor(mouseY / cellSize);
    }
});
canvas.addEventListener("mouseup", () => {
    dragging = false;
    placeBlock();
});

// Game loop
function gameLoop() {
    drawBoard();
    drawBlock(currentBlock, blockPos.x, blockPos.y);
    requestAnimationFrame(gameLoop);
}

gameLoop();
