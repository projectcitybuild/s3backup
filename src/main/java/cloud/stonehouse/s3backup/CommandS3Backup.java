package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3Get;
import cloud.stonehouse.s3backup.s3.S3List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CommandS3Backup implements TabExecutor {

    private final S3Backup s3Backup;

    CommandS3Backup(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;

        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("backup")) {
                if (s3Backup.hasPermission(player, "s3backup.backup")) {
                    new Backup(s3Backup, player).runTaskAsynchronously(s3Backup);
                } else {
                    s3Backup.sendMessage(player, false, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (s3Backup.hasPermission(player, "s3backup.list")) {
                    try {
                        ArrayList<String> backups = s3Backup.getS3List().list();
                        if (backups.size() == 0) {
                            s3Backup.sendMessage(player, true, "There are no backups to list");
                        } else {
                            for (String backup : backups) {
                                s3Backup.sendMessage(player, true, backup);
                            }
                        }
                    } catch (Exception e) {
                        s3Backup.sendMessage(player, false, "Error retrieving backup: " + e.getLocalizedMessage());
                        s3Backup.exception(e);
                    }
                } else {
                    s3Backup.sendMessage(player, false, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (s3Backup.hasPermission(player, "s3backup.delete")) {
                    if (args.length == 2) {
                        s3Backup.getS3Delete().delete(player, args[1]);
                    } else {
                        return false;
                    }
                } else {
                    s3Backup.sendMessage(player, false, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("sign")) {
                if (s3Backup.hasPermission(player, "s3backup.sign")) {
                    if (args.length == 2) {
                        s3Backup.getS3Sign().sign(player, args[1]);
                    } else {
                        return false;
                    }
                } else {
                    s3Backup.sendMessage(player, false, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("get")) {
                if (s3Backup.hasPermission(player, "s3backup.get")) {
                    if (args.length == 2) {
                        new S3Get(s3Backup, player, args[1]).runTaskAsynchronously(s3Backup);
                    } else {
                        return false;
                    }
                } else {
                    s3Backup.sendMessage(player, false, "You do not have permission for this command");
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = null;
        List<String> types = new ArrayList<>();
        types.add("backup");
        types.add("delete");
        types.add("get");
        types.add("list");
        types.add("sign");

        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }

        if (args.length == 1) {
            if (s3Backup.hasPermission(player, "s3backup")) {
                List<String> result = new ArrayList<>();
                if (!args[0].equals("")) {
                    for (String type : types) {
                        if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
                            result.add(type);
                        }
                    }
                    Collections.sort(result);
                    return result;
                } else {
                    Collections.sort(types);
                    return types;
                }
            }
        } else if (args.length == 2) {
            List<String> result = new ArrayList<>();

            if ((args[0].equalsIgnoreCase("get") &&
                    s3Backup.hasPermission(player, "s3backup.get")) ||
                    (args[0].equalsIgnoreCase("delete") &&
                            s3Backup.hasPermission(player, "s3backup.delete")) ||
                    (args[0].equalsIgnoreCase("sign") &&
                            s3Backup.hasPermission(player, "s3backup.sign"))) {

                ArrayList<String> backups = new S3List(s3Backup).list();

                if (!args[1].equals("")) {
                    for (String backup : backups) {
                        if (backup.toLowerCase().startsWith(args[1].toLowerCase())) {
                            result.add(backup);
                        }
                    }
                    Collections.sort(result);
                    return result;
                } else {
                    Collections.sort(backups);
                    return backups;
                }
            }
        }
        return null;
    }
}