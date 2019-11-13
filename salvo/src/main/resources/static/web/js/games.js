
//JQuery VERSION
$(function() {
    loadGames()
    loadLeaderBoard()
});

//LOGIN
$(document).ready(function() {
    $('#login-btn').on('click', function() {

        var user = $('#username').val();
        var pwd = $('#password').val();

        $.post("/api/login", { username: user, password: pwd })
            .done(function() {
                alert('Logged in successfully!');
            }).fail(function() {
                alert('Failed at log');
            });

    });
});

// load and display JSON of Game List and Leaderboard
function loadGames() {
    $.get("/api/games")
        .done(function(data){
            updateGamesView(data);
        })
        .fail(function(jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        });
}

function loadLeaderBoard(){
    $.get("/api/leaderboard")
    .done(function(data){
        updateLeaderboard(data);
    })
    .fail(function(jqXHR, textStatus){
        alert("Failed: " + textStatus);
    });
}

function updateGamesView(data) { //for the JSON Table of current games and their players
    let htmlList = data.games.map(function(games) {
        return '<tr><th scope="row">'+ games.id +'</th><td>' + new Date(games.created).toLocaleString()
        +'</td><td>' + games.gamePlayers.map(function(gp) {
                return gp.player.name
            }).join('  VS  ') + '</td></tr>';
    }).join('');
    document.getElementById("game-list").innerHTML = htmlList;
}

function updateLeaderboard(data){ //for the JSON Leaderboard Table
    var users = data.sort((a,b) => b.total - a.total);

    let htmlList = users.map(function(data){
        return '<tr><th scope="row">'
        + data.user +'</th><td>'
        + data.wins +'</td><td>'
        + data.loses +'</td><td>'
        + data.ties +'</td><td>'
        + data.total + '</td></tr>' }).join('');
    document.getElementById("leaderboard").innerHTML = htmlList;
}

function createGame() {
    $.post("/api/games") //add new game with logged in user as new gameplayer
        .done(function(data) {
            var gpid = data.gamePlayers.gpid;

            console.log('Game created successfully')
        })
        .fail(function(jqXHR, textStatus) {
            alert('Failed: ' + textStatus)
        });
}
