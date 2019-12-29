package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReactionAgent extends AbstractAgent {
    public interface Rule {
        Action match(boolean canMove, boolean dirty, boolean dock, String action);
    }

    private List<Rule> rules = new ArrayList<>();
    private String actions = "";
    private List<Action> makro1 = new ArrayList<>();

    Action action() {
        return makro1.get(new Random().nextInt(makro1.size()));
    }

    public ReactionAgent() {
        makro1.add(Action.TURN_LEFT);
        makro1.add(Action.TURN_RIGHT);

        rules.add((canMove, dirty, dock, action) -> (dirty) ? Action.CLEAN : null);
        rules.add((canMove, dirty, dock, action) -> (dock && actions.length() > 100) ? Action.TURN_OFF : null);
//        rules.add((canMove, dirty, dock, action) -> (canMove && !dirty && !dock) ? Action.TURN_RIGHT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("rrr")) ? Action.TURN_LEFT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("ffff")) ? action() : null);
        rules.add((canMove, dirty, dock, action) -> (canMove) ? action() : null);
        rules.add((canMove, dirty, dock, action) -> (!canMove) ? Action.FORWARD : null);

//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("rll")) ? Action.TURN_LEFT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("ccr")) ? Action.TURN_LEFT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("llr") || (actions.endsWith("llc"))) ? Action.TURN_RIGHT : null);

//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("llll")) ? Action.TURN_LEFT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("ll")) ? Action.TURN_RIGHT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("rrrrr")) ? Action.TURN_LEFT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("llll")) ? Action.TURN_RIGHT : null);
//        rules.add((canMove, dirty, dock, action) -> (actions.endsWith("lllr")) ? Action.TURN_RIGHT : null);
//        rules.add((canMove, dirty, dock, action) -> Action.TURN_LEFT);
    }

    @Override
    public Action doAction(boolean canMove, boolean dirty, boolean dock) {
        for (Rule rule: rules) {
            Action action = rule.match(canMove, dirty, dock, actions);
            if (action != null) {
                if (action != Action.FORWARD) {
                    actions += action.code;
                }
                if (action != Action.CLEAN) {
                    actions += action.code;
                }
                System.out.println(actions);
                return action;
            }
        }
        return Action.TURN_OFF;
    }
}
