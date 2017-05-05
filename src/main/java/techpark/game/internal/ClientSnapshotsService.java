package techpark.game.internal;

import org.springframework.stereotype.Service;
import techpark.game.GameSession;
import techpark.game.avatar.GameUser;
import techpark.game.avatar.Square;
import techpark.game.base.ClientSnap;
import techpark.resources.Generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Варя on 22.04.2017.
 */
@Service
public class ClientSnapshotsService {

    public boolean processSnapshotsFor(GameSession session, ClientSnap snap, GameUser gamer){
        final Generator generator = new Generator();
        if(gamer.getAvaliableGems().size() != (int) generator.settings("gemsPerRound")){
           return processFirstPart(session, snap, gamer);
        }
        else {
           return processSecondtPart(session, snap, gamer);
        }
    }

    private boolean processFirstPart(GameSession session, ClientSnap snap, GameUser gamer){
        if (!session.field.setGem(snap.getSquare())) {
            return false;
        }
        if(session.field.calculateRoute().isEmpty()){
            session.field.setGem(snap.getSquare(), 'o');
            return false;
        }
        gamer.setAvaliableGem(snap.getSquare(), session.field.getSquare(snap.getSquare()));
        return true;
    }

    private boolean processSecondtPart(GameSession session, ClientSnap snap, GameUser gamer){
        if(!gamer.getAvaliableGems().containsKey(snap.getSquare())) {
            snap.setStartWave(false);
            return false;
        }
        setGem(session, snap, gamer);
        gamer.clearAvaliableGems();
        gamer.setReady();
        return true;
    }

    private void setGem(GameSession session, ClientSnap snap, GameUser gamer) {
        if(snap.getComb() != null){
            computeCombination(session, snap, gamer.getAvaliableGems());
        }
        else {
            session.field.addAvailableGem(snap.getSquare(), gamer.getAvaliableGems().get(snap.getSquare()));
            gamer.delAvailableGems(snap.getSquare());
            session.field.setStones(gamer.getAvaliableGems().keySet());
        }
    }

    @SuppressWarnings("unchecked")
    private void computeCombination(GameSession session, ClientSnap snap, Map<Square, Character> gamerGems) {
        final Map<Square, Character> allGems = new HashMap<>();
        allGems.putAll(session.field.getAvaliableGems());
        allGems.putAll(gamerGems);
        final Generator generator = new Generator();
        final HashMap<Character, List<Character>> comb = generator.combinations();
        for(Character gem: comb.get(snap.getComb())) {
            for (Map.Entry<Square, Character> agem : allGems.entrySet()) {
                if (!gem.equals(session.field.getSquare(snap.getSquare())) &&
                        agem.getValue().equals(gem)) {
                    session.field.setStone(agem.getKey());
                }
            }
        }
        session.field.setGem(snap.getSquare(), snap.getComb());
        session.field.addAvailableGem(snap.getSquare(), snap.getComb());
    }
}
