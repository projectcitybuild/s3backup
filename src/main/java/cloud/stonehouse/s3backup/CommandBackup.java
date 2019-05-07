package cloud.stonehouse.s3backup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandBackup implements TabExecutor {

    private S3Backup s3Backup;

    public CommandBackup(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;

        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (args.length == 0) {
            new Backup(s3Backup, player).runTaskAsynchronously(s3Backup);
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                try {
                    ArrayList<String> backups = new S3List(s3Backup).list();
                    if (backups.size() == 0) {
                        if (player != null) {
                            player.sendMessage("§7[§es3backup§7] There are no backups to list.");
                        } else {
                            s3Backup.getLogger().info("There are no backups to list.");
                        }
                    } else {
                        for (String backup : backups) {
                            if (player != null) {
                                player.sendMessage("§7[§es3backup§7] " + backup);
                            } else {
                                s3Backup.getLogger().info(backup);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (player != null) {
                        player.sendMessage("§7[§es3backup§7] Error retrieving backup list: " + e.getLocalizedMessage());
                    } else {
                        s3Backup.exception(e);
                    }
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    new S3Delete(s3Backup, player, args[1]).delete();
                } else {
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("get")) {
                if (args.length == 2) {
                    new S3Get(s3Backup, player, args[1]).runTaskAsynchronously(s3Backup);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("s3backup")) {
            List<String> types = new ArrayList<>();
            types.add("list");
            types.add("get");
            types.add("delete");

            if (args.length == 1) {
                List<String> result = new ArrayList<>();

                if (!args[0].equals("")) {
                    for (String type : types) {
                        if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
                            result.add(type);
                        }
                    }
                    return result;
                } else {
                    return types;
                }
            } else if (args.length == 2) {
                List<String> result = new ArrayList<>();

                if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("delete")) {
                    ArrayList<String> backups = new S3List(s3Backup).list();
                    if (!args[1].equals("")) {
                        for (String backup : backups) {
                            if (backup.toLowerCase().startsWith(args[1].toLowerCase())) {
                                result.add(backup);
                            }
                        }
                        return result;
                    } else {
                        return backups;
                    }
                }
            }
        }

        return null;
    }
}