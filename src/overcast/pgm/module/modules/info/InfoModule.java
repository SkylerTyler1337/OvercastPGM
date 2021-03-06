package overcast.pgm.module.modules.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import overcast.pgm.builder.Builder;
import overcast.pgm.module.Module;
import overcast.pgm.module.ModuleInfo;
import overcast.pgm.util.BukkitUtils;
import overcast.pgm.util.TeamUtil;

@ModuleInfo(name = "info", desc = "info about the map")
public class InfoModule extends Module {

	private String name;
	private String objective;
	private Version version;
	private List<Author> authors;
	private List<Contributor> contributors;
	private List<Rule> rules;
	private Version proto;

	private HashMap<String, String> names;
	/** authors names clearing */
	private HashMap<String, String> contributorNames;
	/** friendlyfire */
	private boolean friendlyfire;

	public InfoModule(Version proto, String name, String objective, Version version, List<Author> authors,
			List<Contributor> contributors, List<Rule> rules, boolean friendlyfire) {
		this.names = new HashMap<>();
		this.contributorNames = new HashMap<>();
		this.proto = proto;
		this.name = name;
		this.objective = objective;
		this.version = version;
		this.authors = authors;
		this.contributors = contributors;
		this.rules = rules;
		this.friendlyfire = friendlyfire;

		addAuthorNames();

		addContributorNames();
	}

	/** make this work on the loaded maps page */

	public void addAuthorNames() {
		for (Author author : this.authors) {
			String name = author.getName();
			this.names.put(name, author.hasContribution() ? author.getContribution() : null);
		}
	}

	/** make this work on the loaded maps page */
	public void addContributorNames() {
		if (hasContributors()) {
			for (Contributor contributor : this.contributors) {
				String name = contributor.getName();
				if (name != null) {
					this.contributorNames.put(name,
							contributor.hasContribution() ? contributor.getContribution() : null);
				}
			}
		}
	}

	public String getAuthor(String name) {
		for (Entry<String, String> entries : this.names.entrySet()) {
			if (entries.getKey().equals(entries)) {
				return name;
			}
		}
		return null;
	}

	public void clearContributorNames() {
		if (this.contributorNames.size() > 0) {
			this.contributorNames.clear();
		}
	}

	public void clearAuthorNames() {
		this.names.clear();
	}

	public boolean hasContributors() {
		return this.contributors.size() >= 1;
	}

	public HashMap<String, String> getAuthorNames() {
		return this.names;
	}

	public Version getVersion() {
		return this.version;
	}

	public String getName() {
		return this.name;
	}

	public String getObjective() {
		return this.objective;
	}

	public List<Author> getAuthors() {
		return this.authors;
	}

	public List<Contributor> getContributors() {
		return this.contributors;
	}

	public boolean isAuthor(Player player) {
		for (Author author : this.getAuthors()) {
			if (author.getUUID().equals(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public boolean isFriendlyFireEnabled() {
		return this.friendlyfire;
	}

	public boolean isAuthor(String name) {
		for (Entry<String, String> author : this.names.entrySet()) {
			if (author.getKey().equals(name) && author != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isContributor(Player player) {
		for (Contributor contrib : this.getContributors()) {
			if (contrib.getUUID().equals(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public Version getProto() {
		return this.proto;
	}

	public List<Rule> getRules() {
		return this.rules;
	}

	@Override
	public Class<? extends Builder> builder() {
		return InfoBuilder.class;
	}

	public String getFormattedMapTitle() {
		return BukkitUtils.dashedChatMessage(
				ChatColor.DARK_AQUA + " " + this.name + ChatColor.GRAY + " " + this.version, "-",
				ChatColor.RED + "" + ChatColor.STRIKETHROUGH);
	}

	public void getMapInformation(Player p) {
		p.sendMessage(
				ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Objective: " + ChatColor.GOLD + this.objective);
		if (this.authors.size() == 1) {
			boolean contribution = this.names.values().toArray()[0] != null;
			String author = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Author: " + ChatColor.DARK_AQUA
					+ this.names.keySet().toArray()[0];
			if (contribution) {
				author += ChatColor.GRAY + " - " + this.names.values().toArray()[0];
			}
			p.sendMessage(author);
		} else {
			p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Authors: ");

			for (Entry<String, String> authorNames : this.names.entrySet()) {
				String string = "* " + ChatColor.DARK_AQUA + authorNames.getKey();

				if (authorNames.getValue() != null) {
					string += ChatColor.GRAY + " - " + authorNames.getValue();

				}
				p.sendMessage(string);
			}
		}

		if (hasContributors()) {
			p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Contributor"
					+ (this.contributors.size() != 1 ? "s" : "") + ":");
			for (Entry<String, String> contributors : this.contributorNames.entrySet()) {
				String key = "* " + ChatColor.DARK_AQUA + contributors.getKey();

				if (contributors.getValue() != null) {
					key += ChatColor.GRAY + " - " + contributors.getValue();
				}

				p.sendMessage(key);
			}
		}

		if (hasRules()) {
			p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Rules: ");
			int i = 0;
			for (Rule r : this.rules) {
				String rule = (++i) + ") " + ChatColor.GOLD + r.getRule();
				p.sendMessage(rule);
			}
		}

		p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Max players: " + ChatColor.RESET + ""
				+ ChatColor.GOLD + TeamUtil.getMaxPlayers());
	}

	public boolean hasRules() {
		return this.rules.size() > 0;
	}
}
