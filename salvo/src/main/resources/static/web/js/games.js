$(function() {
    loadData()
});

$(document).ready(function(){
    $('#login-btn').on('click',function(){

        var user = $('#username').val();
        var pwd = $('#password').val();

       $.post("/api/login", { username: user, password: pwd })
               .done(function(){
                   var msg = '<h2>Logged in!</h2>';
                   $('#log').html(msg);

               }).fail(function(){
                   alert('Failed at log');
               });

    });
});

// load and display JSON sent by server for /players

function loadData() {
    $.get("/api/games")
        .done(function(data) {
          updateView(data);
        })
        .fail(function( jqXHR, textStatus ) {
          alert( "Failed: " + textStatus );
        });
 }

function updateView(data) {
    let htmlList = data.games.map(function (games) {
        return '<li>' + new Date(games.creationDate).toLocaleString() + ' ' +
            games.gamePlayers.map(function(gp) {
                return gp.player.userName
                }).join(' , ')  + '</li>';
    }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}