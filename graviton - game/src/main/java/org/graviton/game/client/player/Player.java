package org.graviton.game.client.player;

import lombok.Data;
import org.graviton.game.creature.Creature;
import org.graviton.converter.Converters;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.breeds.AbstractBreed;
import org.graviton.game.breeds.models.Enutrof;
import org.graviton.game.breeds.models.Sacrieur;
import org.graviton.game.client.account.Account;
import org.graviton.game.creature.monster.extra.Double;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.fight.Fighter;
import org.graviton.game.group.Group;
import org.graviton.game.guild.Guild;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.game.interaction.Status;
import org.graviton.game.inventory.Inventory;
import org.graviton.game.inventory.PlayerStore;
import org.graviton.game.items.Item;
import org.graviton.game.items.StoreItem;
import org.graviton.game.job.Job;
import org.graviton.game.job.JobTemplate;
import org.graviton.game.look.AbstractLook;
import org.graviton.game.look.PlayerLook;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.spell.Spell;
import org.graviton.game.spell.SpellList;
import org.graviton.game.spell.SpellView;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.PlayerStatistics;
import org.graviton.game.zaap.Zaap;
import org.graviton.game.zaap.Zaapi;
import org.graviton.network.game.protocol.*;
import org.graviton.utils.Utils;
import org.jooq.Record;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 05/11/2016 : 22:57
 */
@Data
public class Player extends Fighter implements Creature {
    private final EntityFactory entityFactory;
    private final int id;

    private Account account;

    private final String name;
    private final PlayerLook look;
    private final PlayerStatistics statistics;
    private final Alignment alignment;
    private final Inventory inventory;
    private final SpellList spellList;
    private final PlayerStore store;

    private boolean online;
    private Location location;
    private Location savedLocation;

    private Group group;
    private Exchange exchange;
    private Guild guild;

    private List<Player> followers;

    private List<Integer> zaaps;

    private Map<Short, Job> jobs;

    public Player(Record record, Account account, Map<Integer, Item> items, List<StoreItem> store, Map<Short, SpellView> spells, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.account = account;
        this.id = record.get(PLAYERS.ID);
        this.name = record.get(PLAYERS.NAME);

        this.inventory = new Inventory(this, record.get(PLAYERS.KAMAS), items);
        this.look = new PlayerLook(record);
        this.statistics = new PlayerStatistics(this, record, (byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignment = new Alignment(record.get(PLAYERS.ALIGNMENT), record.get(PLAYERS.HONOR), record.get(PLAYERS.DISHONNOR), record.get(PLAYERS.PVP_ENABLED) == 1, entityFactory);
        this.location = new Location(entityFactory.getMap(record.get(PLAYERS.MAP)), record.get(PLAYERS.CELL), record.get(PLAYERS.ORIENTATION));
        this.savedLocation = new Location(record.get(PLAYERS.SAVEDLOCATION), entityFactory);
        this.spellList = new SpellList(spells);
        this.store = new PlayerStore(this, store);

        this.guild = entityFactory.getGuildRepository().find(record.get(PLAYERS.GUILD));

        this.followers = new ArrayList<>();
        this.zaaps = Utils.parseZaaps(record.get(PLAYERS.WAYPOINTS));

        String job = record.get(PLAYERS.JOB);

        this.jobs = job.isEmpty() ? new HashMap<>() : Stream.of(job.split(";")).collect(Collectors.toMap(data -> Short.parseShort(data.split(",")[0]), data -> {
            String[] jobData = data.split(",");
            return new Job(entityFactory.getJobTemplate(Integer.parseInt(jobData[0])), Long.parseLong(jobData[1]), entityFactory);
        }));
    }

    public Player(int id, String data, Account account, EntityFactory entityFactory) {
        account.getClient().send(GamePacketFormatter.creationAnimationMessage());

        this.entityFactory = entityFactory;
        String[] information = data.split("\\|");

        this.account = account;
        this.id = id;
        this.name = information[0];

        this.look = new PlayerLook(Utils.parseColors(information[3] + ";" + information[4] + ";" + information[5], ";"), Byte.parseByte(information[2]), AbstractBreed.get(Byte.parseByte(information[1])));
        this.statistics = new PlayerStatistics(this, (byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignment = new Alignment((byte) 0, 0, 0, false, entityFactory);
        this.location = new Location(entityFactory.getMap(getBreed().incarnamMap()), getBreed().incarnamCell(), (byte) 1);
        this.savedLocation = this.location.copy();

        this.store = new PlayerStore(this, new ArrayList<>());
        this.inventory = new Inventory(this);

        this.spellList = new SpellList(new TreeMap<>());

        IntStream.range(0, 3).forEach(i -> this.spellList.add(new SpellView((byte) (i + 1),
                entityFactory.getSpellTemplate(getBreed().getStartSpells()[i]).getLevel((byte) 1), (byte) (i + 1))));

        this.followers = new ArrayList<>();
        this.zaaps = new ArrayList<>();
        this.jobs = new HashMap<>();
    }

    public InteractionManager interactionManager() {
        return account.getClient().getInteractionManager();
    }

    public int getColor(byte color) {
        return this.look.getColors()[color - 1];
    }

    @Override
    public EntityFactory entityFactory() {
        return this.entityFactory;
    }

    @Override
    public AbstractLook look() {
        return this.look;
    }

    public int[] getColors() {
        return this.look.getColors();
    }

    public AbstractBreed getBreed() {
        return this.look.getBreed();
    }

    public byte getTitle() {
        return this.look.getTitle();
    }

    public short getSkin() {
        return this.look.getSkin();
    }

    public short getSize() {
        return this.look.getSize();
    }

    public byte getSex() {
        return this.look.getSex();
    }

    public Orientation getOrientation() {
        return this.location.getOrientation();
    }

    public String compileSavedLocation() {
        return savedLocation.getMap().getId() + ";" + savedLocation.getCell().getId();
    }

    public String compileJobs() {
        if (jobs.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        jobs.forEach((jobId, job) -> builder.append(jobId).append(",").append(job.getExperience()).append(";"));
        return builder.substring(0, builder.length() - 1);
    }

    public String compileStore() {
        if (store.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        store.forEach(item -> builder.append(item.getId()).append(":").append(item.getPrice()).append(";"));
        return builder.substring(0, builder.length() - 1);
    }

    public long getExperience() {
        return this.statistics.getExperience();
    }

    public short getLevel() {
        return this.statistics.getLevel();
    }

    public short[] getPods() {
        return this.statistics.getPods();
    }

    public AbstractMap getMap() {
        return this.location.getMap();
    }

    public GameMap getGameMap() {
        return (GameMap) this.location.getMap();
    }

    public Cell getCell() {
        return this.location.getCell();
    }

    @Override
    public String getGm() {
        return PlayerPacketFormatter.gmMessage(this);
    }

    @Override
    public void send(String data) {
        this.account.getClient().send(data);
    }

    public void sendSplit(String data) {
        Stream.of(data.split("\n")).forEach(this::send);
    }

    @Override
    public String getFightGM() {
        return PlayerPacketFormatter.fightGmMessage(this);
    }

    @Override
    public String doubleGm(Double clone) {
        return PlayerPacketFormatter.fightCloneGmMessage(this, clone);
    }

    @Override
    public ArtificialIntelligence artificialIntelligence() {
        return null;
    }

    @Override
    public List<Spell> getSpells() {
        return spellList.getSpells();
    }

    public void returnToLastLocation() {
        changeMap((GameMap) savedLocation.getMap(), savedLocation.getCell().getId());
    }

    public void changeMap(Zaap zaap) {
        changeMap(zaap.getGameMap(), zaap.getCell());
    }

    public void changeMap(Zaapi zaapi) {
        changeMap(zaapi.getGameMap(), zaapi.getCell());
    }

    public void changeMap(int newGameMapId, short newCell) {
        changeMap(entityFactory.getMap(newGameMapId), newCell);
    }

    public void changeMap(GameMap newGameMap, short cell) {
        if (newGameMap == null)
            return;

        if (cell == 0) cell = newGameMap.getRandomCell();

        if (getMap().getId() == newGameMap.getId()) {
            this.location.setCell(newGameMap.getCells().get(cell));
            getMap().refreshCreature(this);
        } else {
            if (newGameMap.getZaap() != null && !this.zaaps.contains(newGameMap.getZaap().getGameMap()))
                addZaap(newGameMap.getZaap());

            getMap().out(this);
            this.location.setCell(newGameMap.getCells().get(cell));
            newGameMap.loadAndEnter(this);
        }
        this.entityFactory.getPlayerRepository().save(this);
        this.account.getClient().getInteractionManager().clear();

        followers.forEach(follower -> follower.send(PartyPacketFormatter.flagMessage(getMap().getPosition())));
    }

    private void addZaap(Zaap zaap) {
        zaaps.add(zaap.getGameMap());
        send(MessageFormatter.newZaapMessage());
        update();
    }

    public void removeItem(Item item) {
        if (this.inventory.containsKey(item.getId()))
            this.inventory.remove(item.getId());
        removeDatabaseItem(item);
    }

    public void createItem(Item item) {
        this.entityFactory.getPlayerRepository().createItem(item, this.id);
    }

    private void removeDatabaseItem(Item item) {
        this.entityFactory.getPlayerRepository().removeItem(item);
    }

    @Override
    public Creature getCreature() {
        return this;
    }

    private void upgrade() {
        this.statistics.upLevel();
        learnSpell(getBreed().getSpell(this.getLevel()));
    }

    public void upLevel(boolean upgrade) {
        if (upgrade)
            upgrade();

        send(PlayerPacketFormatter.asMessage(this, entityFactory.getExperience(getLevel()), this.alignment, this.statistics));
        entityFactory.getPlayerRepository().save(this);
        send(PlayerPacketFormatter.nextLevelMessage(this.statistics.getLevel()));

        if (guild != null)
            guild.getMember(this.id).setLevel(getLevel());
    }

    public void addExperience(long experience) {
        this.statistics.addExperience(experience);
    }

    private void learnSpell(short spell) {
        if (spell == 0)
            return;

        short nextId = (short) (this.spellList.size() + 1);
        SpellView spellView = new SpellView(nextId, entityFactory.getSpellTemplate(spell).getLevel((byte) 1), Utils.getNextPosition(Converters.SPELL_TO_BYTE.apply(this.spellList.values())));
        this.spellList.add(spellView);
        send(SpellPacketFormatter.spellListMessage(this.spellList));
        this.entityFactory.getPlayerRepository().createSpell(nextId, spellView, this);
    }

    public void boostStatistics(byte characteristicId) {
        CharacteristicType characteristic = CharacteristicType.getBoost(characteristicId);
        byte cost = (byte) (characteristic == CharacteristicType.Wisdom ? 3 : 1), bonus = 1;

        if (getBreed() instanceof Sacrieur && characteristic == CharacteristicType.Vitality)
            bonus = 2;

        if (cost == 1 && bonus == 1)
            cost = getBreed().boostCost(characteristicId, this.statistics.get(characteristic).base());

        statistics.setStatisticPoints((short) (statistics.getStatisticPoints() - cost));
        statistics.get(characteristic).addBase(bonus);

        if (characteristic == CharacteristicType.Vitality)
            this.statistics.getLife().add(bonus);

        send(PlayerPacketFormatter.asMessage(this, this.entityFactory.getExperience(getLevel()), alignment, statistics));
    }

    public SpellView getSpellView(short spell) {
        return spellList.get(spell);
    }

    public SpellView getSpellView(byte position) {
        return spellList.get(position);
    }

    public void update() {
        this.entityFactory.getPlayerRepository().save(this);
    }

    public boolean isBusy() {
        return account.getClient().getInteractionManager().isBusy();
    }

    public void setStatus(Status status) {
        account.getClient().getInteractionManager().setStatus(status);
    }

    public void changeAlignment(byte newAlignment) {
        this.alignment.changeAlignment(newAlignment);
        this.send(PlayerPacketFormatter.newAlignmentMessage(newAlignment));
        send(PlayerPacketFormatter.asMessage(this, this.entityFactory.getExperience(getLevel()), alignment, statistics));
        update();
    }

    public void switchAlignmentEnabled(char data) {
        if (data == '+')
            this.alignment.setEnabled(true);
        else if (data == '*') {
            send(PlayerPacketFormatter.disableAlignmentMessage((short) (this.alignment.getHonor() * 5 / 100)));
            return;
        } else if (data == '-') {
            this.alignment.setEnabled(false);
            this.alignment.addHonor(this.alignment.getHonor() * -5 / 100);
        }

        send(PlayerPacketFormatter.asMessage(this, this.entityFactory.getExperience(getLevel()), alignment, statistics));
        getGameMap().refreshCreature(this);
        update();
    }

    public void learnJob(JobTemplate jobTemplate) {

    }

}
