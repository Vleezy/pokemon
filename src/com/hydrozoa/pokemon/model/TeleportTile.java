package com.hydrozoa.pokemon.model;

import com.badlogic.gdx.graphics.Color;
import com.hydrozoa.pokemon.model.actor.Actor;
import com.hydrozoa.pokemon.model.actor.PlayerActor;
import com.hydrozoa.pokemon.model.world.Door;
import com.hydrozoa.pokemon.model.world.World;
import com.hydrozoa.pokemon.model.world.cutscene.ActorVisibilityEvent;
import com.hydrozoa.pokemon.model.world.cutscene.ActorWalkEvent;
import com.hydrozoa.pokemon.model.world.cutscene.ChangeWorldEvent;
import com.hydrozoa.pokemon.model.world.cutscene.CutscenePlayer;
import com.hydrozoa.pokemon.model.world.cutscene.DoorEvent;
import com.hydrozoa.pokemon.model.world.cutscene.WaitEvent;

/**
 * @author hydrozoa
 */
public class TeleportTile extends Tile {
	
	/* destination */
	private String worldName;
	private int x, y;
	private DIRECTION facing;
	
	/* transition color */
	private Color color;

	public TeleportTile(TERRAIN terrain, String worldName, int x, int y, DIRECTION facing, Color color) {
		super(terrain);
		this.worldName = worldName;
		this.x= x;
		this.y=y;
		this.facing=facing;
		this.color=color;
	}
	
	@Override
	public void actorStep(Actor a) {
		if (a instanceof PlayerActor) {
			PlayerActor playerActor = (PlayerActor) a; 
			CutscenePlayer cutscenes = playerActor.getCutscenePlayer(); 
			World currentWorld = playerActor.getWorld();

			if (this.getObject() != null) {
				if (this.getObject() instanceof Door) {
					Door door = (Door)currentWorld.getMap().getTile(x, y).getObject();
					cutscenes.queueEvent(new ActorVisibilityEvent(a, true));
					cutscenes.queueEvent(new ChangeWorldEvent(worldName, x, y, facing, color));
					cutscenes.queueEvent(new DoorEvent(door, true));
					cutscenes.queueEvent(new WaitEvent(0.2f));
					cutscenes.queueEvent(new ActorVisibilityEvent(a, false));
					cutscenes.queueEvent(new WaitEvent(0.2f));
					cutscenes.queueEvent(new ActorWalkEvent(a, DIRECTION.SOUTH));
					cutscenes.queueEvent(new DoorEvent(door, false));
				}
			} else {
				cutscenes.queueEvent(new ChangeWorldEvent(worldName, x, y, facing, color));
			}
		}
	}
	
	@Override
	public boolean actorBeforeStep(Actor a) {
		if (a instanceof PlayerActor) {
			PlayerActor playerActor = (PlayerActor) a; 
			CutscenePlayer cutscenes = playerActor.getCutscenePlayer(); 
			if (this.getObject() != null) {
				if (this.getObject() instanceof Door) {
					Door door = (Door)this.getObject();
					cutscenes.queueEvent(new DoorEvent(door, true));
					cutscenes.queueEvent(new ActorWalkEvent(a, DIRECTION.NORTH));
					cutscenes.queueEvent(new ActorVisibilityEvent(a, true));
					cutscenes.queueEvent(new DoorEvent(door, false));
					cutscenes.queueEvent(new ChangeWorldEvent(worldName, x, y, facing, color));
					cutscenes.queueEvent(new ActorVisibilityEvent(a, false));
					cutscenes.queueEvent(new ActorWalkEvent(a, DIRECTION.NORTH));
					return false;
				}
			}
		}
		return true;
	}
}
