package overcast.pgm.module.modules.kits.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta; 
import org.w3c.dom.Element;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import overcast.pgm.module.modules.kits.KitParser;
import overcast.pgm.module.modules.projectiles.custom.ProjectileModule;
import overcast.pgm.util.BukkitUtils;
import overcast.pgm.util.NumberUtils;
import overcast.pgm.util.XMLUtils;
//FIXME projectileModule working with items to make custom projectiles :).

public class ItemKitParser extends KitParser {

	private int slot;
	private ItemStack stack;
	private Map<Enchantment, Integer> enchantments;
	private ItemMeta meta;
	private ProjectileModule projectile;
	private Set<Material> canDestroy;
	private Set<Material> canPlaceOn;

	// TODO need to implement
	private Material[] grenadables = new Material[] { Material.SNOW_BALL, Material.ARROW, Material.ENDER_PEARL,
			Material.FIREBALL };

	/** fix enchantment and durability */
	public ItemKitParser(Element element) {
		super(element);
		this.enchantments = new HashMap<Enchantment, Integer>();
		this.canDestroy = new HashSet<Material>();
		this.canPlaceOn = new HashSet<Material>();
		this.slot = element.hasAttribute("slot") ? NumberUtils.parseInteger(element.getAttribute("slot")) : -1;
		this.projectile = element.hasAttribute("projectile")
				? ProjectileModule.getProjectile(element.getAttribute("projectile")) : null;
		String lore = element.hasAttribute("lore") ? element.getAttribute("lore") : null;
		List<String> newLore = parseLore(lore);
		List<String> colored = BukkitUtils.colorizeList(newLore);
		short damage = element.hasAttribute("damage") ? NumberUtils.parseShort(element.getAttribute("damage")) : 0;
		int amount = element.hasAttribute("amount") ? NumberUtils.parseInteger(element.getAttribute("amount")) : 1;
		String name = element.hasAttribute("name") ? BukkitUtils.colorize(element.getAttribute("name")) : null;
		boolean unbreakable = element.hasAttribute("unbreakable")
				? XMLUtils.parseBoolean(element.getAttribute("unbreakable")) : false;

		this.stack = XMLUtils.parseItem(element.getAttribute("material"), amount, damage);

		// can destroy
		Element canDestroyEl = XMLUtils.getChildElement(element, "can-destroy");

		if (canDestroyEl != null) {
			for (Element matEl : XMLUtils.getUniqueChildren(canDestroyEl, "material")) {
				if (matEl != null) {
					this.canDestroy.add(XMLUtils.parseMaterial(matEl.getTextContent()));
				}
			}
		}

		// can place
		Element canPlaceOnEl = XMLUtils.getChildElement(element, "can-place-on");

		if (canPlaceOnEl != null) {
			for (Element matEL : XMLUtils.getUniqueChildren(canPlaceOnEl, "material")) {
				if (matEL != null) {
					this.canPlaceOn.add(XMLUtils.parseMaterial(matEL.getTextContent()));
				}
			}
		}

		List<Element> children = XMLUtils.getChildElements(element);

		for (Element child : children) {
			if (child != null) {
				switch (child.getTagName()) {
				case "enchantment":
					int level = child.hasAttribute("level") ? NumberUtils.parseInteger(child.getAttribute("level")) : 1;
					this.enchantments.put(XMLUtils.parseEnchantment(child.getTextContent()), level);
					break;
				}
			}
		}

		// FIXME the durability not becoming unbreakable
		short durability = 0;
		if (unbreakable) {
			durability = -1500;
		}

		this.stack.setDurability(durability);

		this.meta = this.stack.getItemMeta();

		boolean hasColor = element.hasAttribute("color");

		if (this.meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) this.meta;
			if (hasColor) {
				armorMeta.setColor(XMLUtils.parseColor(element.getAttribute("color")));
			}
		} 

		this.meta.setLore(colored);
		this.meta.setDisplayName(name);

		if (!this.enchantments.isEmpty()) {
			for (Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
				this.meta.addEnchant(entry.getKey(), entry.getValue(), true);
			}
		}

		this.stack.setItemMeta(this.meta);
	}

	public static List<String> parseLore(String lore) {
		List<String> lores = new ArrayList<>();
		if (lore != null) {

			if (lore.contains("|")) {
				List<String> splitter = ImmutableList.copyOf(Splitter.on("|").split(lore));

				for (String splitted : splitter) {
					if (splitted != null) {
						lores.add(splitted);
					}
				}
			} else {
				lores.add(lore);
			}
		}
		return lores;
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}

	public ItemMeta getItemMeta() {
		return this.meta;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return this.enchantments;
	}

	public ProjectileModule getProjectile() {
		return this.projectile;
	}

	public Set<Material> canDestroy() {
		return this.canDestroy;
	}

	public Set<Material> canPlaceOn() {
		return this.canPlaceOn;
	}
}
