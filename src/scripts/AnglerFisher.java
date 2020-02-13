package scripts;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;


public class AnglerFisher extends Script {

    private ABCUtil antiBan;
    private RSArea bankArea;
    private RSArea fishingArea;
    private boolean isHovering;
    private ABCProperties aBan;
    private boolean openMenu;
    private boolean shouldHover;
    private int fishCount;
    private long fishingTime;
    private long storedTime;

    public static void main(String[] args) {
    }

    public AnglerFisher() {
        this.antiBan = new ABCUtil();
        this.bankArea = new RSArea(new RSTile(1795, 3790, 0), new RSTile(1814, 3779, 0));
        this.fishingArea = new RSArea(new RSTile(1822, 3774, 0), new RSTile(1839, 3776, 0));
        this.aBan = antiBan.getProperties();
        this.isHovering = aBan.isHovering();
        this.openMenu = false;
        this.shouldHover = false;
        this.fishCount = 10;
        this.storedTime = System.currentTimeMillis();
        this.fishingTime = System.currentTimeMillis() - storedTime;
    }

    @Override
    public void run() {

        General.useAntiBanCompliance(true);
        while (true) {


            fish();
            sleep();
            generateSupportingTrackerInformation();

            if (Inventory.isFull()) {
                bank();
                generateSupportingTrackerInformation();
                antiBan.close();
                WebWalking.walkTo(fishingArea.getRandomTile());
            }

        }

    }


    public void fish() {

        if (!isFishing()) {

            openMenuForNextTarget();

            if (this.antiBan.shouldCheckTabs())
                this.antiBan.checkTabs();

            if (this.antiBan.shouldCheckXP())
                this.antiBan.checkXP();

            if (this.antiBan.shouldExamineEntity())
                this.antiBan.examineEntity();

            if (this.antiBan.shouldMoveMouse())
                this.antiBan.moveMouse();

            if (this.antiBan.shouldPickupMouse())
                this.antiBan.pickupMouse();

            if (this.antiBan.shouldRightClick())
                this.antiBan.rightClick();

            if (this.antiBan.shouldRotateCamera())
                this.antiBan.rotateCamera();

            if (this.antiBan.shouldLeaveGame())
                this.antiBan.leaveGame();


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

    public void handleFishingWhile(Boolean fishingStatus) {
        if (Mouse.isInBounds() && this.isHovering) {


            if (this.openMenu)
                openMenuForNextTarget();
        }

    }

    public void openMenuForNextTarget() {


        if (DynamicClicking.clickRSNPC(findNextTarget(), "Bait")) {
            this.openMenu = this.antiBan.shouldOpenMenu();
            this.shouldHover = this.antiBan.shouldHover();
        }


    }


    public RSNPC findNextTarget() {
        RSNPC[] fishingSpot = NPCs.findNearest(6825);
        if (fishingSpot == null || fishingSpot.length < 1) {
            throw new NullPointerException();
        }


        return (RSNPC) this.antiBan.selectNextTarget(fishingSpot);


    }

    public void sleep() {


        final int waitingTime = getWaitingTime();
        final boolean menuOpen = this.antiBan.shouldOpenMenu() && this.antiBan.shouldHover();
        final boolean hovering = this.antiBan.shouldHover();
        final ABCProperties props = this.antiBan.getProperties();
        props.setWaitingTime(waitingTime);
        props.setHovering(hovering);
        props.setMenuOpen(menuOpen);
        props.setUnderAttack(Combat.isUnderAttack());
        props.setWaitingFixed(false);

        final int reaction_time = this.antiBan.generateReactionTime();

        try {
            this.antiBan.sleep(reaction_time);
        } catch (final InterruptedException e) {

        }

    }

    public int getWaitingTime() {
        return this.aBan.getWaitingTime();
    }

    public boolean successfullyClickedFish() {
        return DynamicClicking.clickRSNPC(findNextTarget(), "Bait");
    }


    public void generateSupportingTrackerInformation() {


        if (successfullyClickedFish()) {


            final int est_waiting;
            if (this.fishCount > 0)
                est_waiting = (int) (fishingTime / this.fishCount);
            else est_waiting = 3000;
            final ABCProperties props = this.antiBan.getProperties();
            props.setWaitingTime(est_waiting);
            props.setUnderAttack(false);
            props.setWaitingFixed(false);
            this.antiBan.generateTrackers();
            while (isFishing()) {
                if (this.antiBan.shouldLeaveGame())
                    this.antiBan.shouldLeaveGame();
                this.antiBan.shouldHover();
            }


        }
    }

    public boolean isFishing() {

        return Player.getAnimation() == 622 || Player.getAnimation() == 623;


    }


}