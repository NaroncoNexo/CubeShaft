/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft;

import java.util.List;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.phys.AABB;

public class Entity {
	public Level level;
	public float xo;
	public float yo;
	public float zo;
	public float x;
	public float y;
	public float z;
	public float xd;
	public float yd;
	public float zd;
	public float xRotO;
	public float yRotO;
	public float xRot;
	public float yRot;
	public AABB aabb;
	public boolean onGround = false;
	public boolean collision = false;
	public boolean removed = false;
	public float heightOffset = 0.0f;
	private float bbWidth = 0.6f;
	public float bbHeight = 1.8f;

	public Entity(Level level) {
		this.level = level;
		resetPos();
	}

	public void resetPos() {
		float xs = (float) Math.random() * (level.width - 2) + 1.0f;
		float ys = level.height + 10;
		float zs = (float) Math.random() * (level.depth - 2) + 1.0f;
		setPos(xs, ys, zs);
	}

	public void setSize(float width, float height) {
		this.bbWidth = width;
		this.bbHeight = height;
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		float wh = bbWidth / 2.0f;
		float hh = bbHeight / 2.0f;
		aabb = new AABB(x - wh, y - hh, z - wh, x + wh, y + hh, z + wh);
	}

	public void tick() {
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	public void collide(Entity e) {
	}

	public boolean blocks(Entity e) {
		return true;
	}

	public boolean isFree(float xd, float yd, float zd) {
		AABB bb = new AABB(aabb.x0 + xd, aabb.y0 + yd, aabb.z0 + zd, aabb.x1
				+ xd, aabb.y1 + yd, aabb.z1 + zd);
		return level.getCubes(this, bb).size() == 0
				&& (!level.containsLiquid(bb, 1) && !level
						.containsLiquid(bb, 2));
	}

	public void move(float xd, float yd, float zd) {
		float xdo = xd;
		float ydo = yd;
		float zdo = zd;

		AABB tmpBB = aabb.copie();
		tmpBB.move(xd, 0.0f, 0.0f);
		List<AABB> cubes = this.level.getCubes(this, tmpBB);
		if (cubes.size() > 0) {
			this.xd = xd = 0.0f;
		}
		this.aabb.move(xd, 0.0f, 0.0f);

		tmpBB = aabb.copie();
		tmpBB.move(0.0f, yd, 0.0f);
		cubes = this.level.getCubes(this, tmpBB);
		if (cubes.size() > 0) {
			this.yd = yd = 0.0f;
		}
		this.aabb.move(0.0f, yd, 0.0f);

		tmpBB = aabb.copie();
		tmpBB.move(0.0f, 0.0f, zd);
		cubes = this.level.getCubes(this, tmpBB);
		if (cubes.size() > 0) {
			this.zd = zd = 0.0f;
		}
		this.aabb.move(0.0f, 0.0f, zd);

		collision = (xd != xdo) || (zd != zdo);
		onGround = (ydo != yd && ydo < 0.0f);
		if (xdo != xd)
			this.xd = 0.0f;
		if (ydo != yd)
			this.yd = 0.0f;
		if (zdo != zd)
			this.zd = 0.0f;
		this.x = (this.aabb.x0 + this.aabb.x1) / 2.0f;
		this.y = this.aabb.y0 + this.heightOffset;
		this.z = (this.aabb.z0 + this.aabb.z1) / 2.0f;

		synchronized (level.entities) {
			for (Entity e : level.entities) {
				if (this == e || !this.blocks(e))
					continue;
				AABB bb = e.aabb;
				if (bb.intersects(aabb)) {
					this.collide(e);
				}
			}
		}
	}

	public boolean isInWater() {
		return this.level.containsLiquid(this.aabb.grow(0.0f, -0.4f, 0.0f), 1);
	}

	public boolean isInLava() {
		return this.level.containsLiquid(this.aabb, 2);
	}

	public void moveRel(float xa, float za, float speed) {
		float dist = xa * xa + za * za;
		if (dist < 0.01f) {
			return;
		}
		float m = speed / (float) Math.sqrt(dist);
		xa *= m;
		za *= m;
		float sin = (float) Math.sin(this.xRot * Math.PI / 180.0);
		float cos = (float) Math.cos(this.xRot * Math.PI / 180.0);
		this.xd += xa * cos - za * sin;
		this.zd += za * cos + xa * sin;
	}

	public void render(float delta) {
	}

	public void hit(Entity e, int dmg) {
	}
}
