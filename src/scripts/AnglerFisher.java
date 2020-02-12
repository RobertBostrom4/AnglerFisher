package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api.util.abc.preferences.OpenBankPreference;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;


public class AnglerFisher extends Script {

    private ABCUtil antiBan;
    private RSArea bankArea;
    private RSArea fishingArea;

    public static void main(String[] args) {

    }

    public AnglerFisher() {
        this.antiBan = new ABCUtil();
        this.bankArea = new RSArea(new RSTile(1793, 3796, 0), new RSTile(1814, 3779, 0));
        this.fishingArea = new RSArea(new RSTile(1822, 3774, 0), new RSTile(1839, 3776, 0));
    }

    @Override
    public void run() {

        General.useAntiBanCompliance(true);


        while (true) {

            fish();

            if (Inventory.isFull()) {
                bank();
                antiBan.close();
                WebWalking.walkTo(fishingArea.getRandomTile());
            }


        }


    }


    public void fish() {


        while (true) {
            RSNPC[] fishingSpot = NPCs.findNearest(100, 6825);
            if (fishingSpot.length > 0) {

                if (Inventory.isFull()) {
                    break;
                }

                this.antiBan.selectNextTarget(fishingSpot);
                this.antiBan.shouldHover();
                DynamicClicking.clickRSNPC(fishingSpot[0], "Bait");
                this.antiBan.leaveGame();
                Timing.waitCondition(() -> Player.getAnimation() == -1, 60000);


            } else {

                System.out.println("Target not found");
            }


        }


    }

    public void bank() {


        if (!bankArea.contains(Player.getPosition())) {

            antiBan.shouldHover();
            if (WebWalking.walkTo(bankArea.getRandomTile())) {
                Timing.waitCondition(() -> bankArea.contains(Player.getPosition()), General.random(5000, 6000));
            }


        }

        if (bankArea.contains(Player.getPosition())) {

            antiBan.shouldHover();
            Banking.openBank();
            Banking.deposit(100, 13439);
            Banking.close();

        }


    }


}
