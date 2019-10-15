package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/players")
     public List<Object> getPlayers(){ return playerRepository.findAll().stream().collect(toList());
    }

    @RequestMapping("/games")
    public Map<String,Object> getGame(Authentication authentication){
        Map <String,Object> dto = new LinkedHashMap<>();
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
        return gameViewDTO(gamePlayerRepository.getOne(gamePlayerId));
    }

    @RequestMapping("/leaderboard")
    public List<Map<String,Object>> getLeaderBoard(){
        return playerRepository.findAll().stream().map(Player::makeLeaderBoardDTO).collect(toList());
    }

    public Map<String,Object> gameViewDTO(GamePlayer gamePlayer){
        Map<String,Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("creationDate", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers",gamePlayer.getGame().getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO));
        dto.put("ships",gamePlayer.getShips().stream().map(Ship::makeShipDTO));
        dto.put("salvoes",gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(Salvo::makeSalvoDTO)).collect(Collectors.toList()));
        return dto;
    }


    public List <Map<String,Object>> getShipList(Set<Ship> ships){
        return ships.stream().map(Ship::makeShipDTO).collect(Collectors.toList());
    }
    public List<Map<String,Object>> getSalvoList(Set<Salvo> salvoes){
        return salvoes.stream().map(Salvo::makeSalvoDTO).collect(Collectors.toList());
    }


}

