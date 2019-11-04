package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.management.ObjectName;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@RestController
@RequestMapping("/api")
public class SalvoController {

  @Autowired
  private GameRepository gameRepository;
  @Autowired
  private PlayerRepository playerRepository;
  @Autowired
  private GamePlayerRepository gamePlayerRepository;
  @Autowired
  private ShipRepository shipRepository;
  @Autowired
  private SalvoRepository salvoRepository;
  @Autowired
  private ScoreRepository scoreRepository;


  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<String> createPlayer(@RequestParam String name,@RequestParam String password) {

    if(name.isEmpty()){
      return new ResponseEntity<String>("No name given",HttpStatus.FORBIDDEN);
    }

    Player player = playerRepository.findByUserName(name);

    if(name != null){ //todo esta bien
      playerRepository.save(new Player(name, password));
      return new ResponseEntity<String>("Player created", HttpStatus.CREATED);
    }else{
      return new ResponseEntity<String>("The user already exists",HttpStatus.CONFLICT);
    }

  }

  @RequestMapping(path="/", method = RequestMethod.POST)
  public ResponseEntity<String> createGame(Player player){

  }

  @RequestMapping("/games")
  public Map<String,Object>> getGame(Authentication authentication){
    Map<String,Object> dto = new LinkedHashMap<>();

    if(isGuest(authentication)){
      dto.put("player", "Guest");
    }else{
      Player player = playerRepository.findByUserName(authentication.getName());
      dto.put("player", player.makePlayerDTO());
    }

    dto.put("games", gameRepository.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList()));
    return dto;
  }

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }


  @RequestMapping("/game_view/{gamePlayerId}")
  public Map<String,Object> getGameView(@PathVariable Long gamePlayerId){
    return gameViewDTO(gamePlayerRepository.findById(gamePlayerId).orElse(null));
  }


  @RequestMapping("/leaderboard")
  public List<Map<String,Object>> getLeaderBoard(){
    return playerRepository.findAll().stream().map(Player::makeLeaderBoardDTO).collect(toList());
  }

  //DTO GAMEVIEW

  public Map<String,Object> gameViewDTO(GamePlayer gamePlayer){
    Map<String,Object> dto = new LinkedHashMap<>();

    dto.put("id", gamePlayer.getGame().getId());
    dto.put("created", gamePlayer.getGame().getCreationDate());
    dto.put("gamePlayers",gamePlayer.getGame().getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO));
    dto.put("ships",gamePlayer.getShips().stream().map(Ship::makeShipDTO));
    dto.put("salvoes",gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::makeSalvoDTO)).collect(Collectors.toList()));
    return dto;
  }

  //SHIP LIST
  public List <Map<String,Object>> getShipList(Set<Ship> ships){
    return ships.stream().map(Ship::makeShipDTO).collect(Collectors.toList());
  }


}

