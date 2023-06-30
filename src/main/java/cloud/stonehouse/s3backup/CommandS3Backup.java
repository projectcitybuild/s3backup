package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3Get;
import cloud.stonehouse.s3backup.s3.S3List;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

class CommandS3Backup implements TabExecutor {

    private final S3Backup s3Backup;
    private final String helpString;

    CommandS3Backup(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.helpString = s3Backup.getFileConfig().getHelpString();
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
                    if (args.length == 2) {
                        String name = args[1];
                        try {
                            if (s3Backup.illegalString(name)) {
                                throw new StringFormatException("Invalid backup name. Only alphanumeric characters, " +
                                        "underscores and hyphens are permitted");
                            } else {
                                BukkitTask task = new Backup(s3Backup, player, name + "-", false).runTaskAsynchronously(s3Backup);
                                s3Backup.setCurrentTask(task);
                            }
                        } catch (StringFormatException e) {
                            s3Backup.exception(player, "Error", e);
                        }
                    } else {
                        BukkitTask task = new Backup(s3Backup, player, "manual-", false).runTaskAsynchronously(s3Backup);
                        s3Backup.setCurrentTask(task);
                    }
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (s3Backup.hasPermission(player, "s3backup.cancel")) {
                    s3Backup.sendMessage(player, "Attempting to cancel backup");
                    s3Backup.getCurrentTask().cancel();
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("dry-run")) {
                if (s3Backup.hasPermission(player, "s3backup.backup")) {
                    BukkitTask task = new Backup(s3Backup, player, "", true).runTaskAsynchronously(s3Backup);
                    s3Backup.setCurrentTask(task);
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (s3Backup.hasPermission(player, "s3backup.list")) {
                    int limit = 0;

                    if (args.length == 2) {
                        try {
                            limit = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            s3Backup.sendMessage(player, "Invalid number to list");
                            return false;
                        }
                    }
                    TreeMap<Date, S3ObjectSummary> backups = s3Backup.s3List().list(player, true, limit);
                    if (backups.size() == 0) {
                        s3Backup.sendMessage(player, "There are no backups to list");
                    }
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (s3Backup.hasPermission(player, "s3backup.delete")) {
                    if (args.length == 2) {
                        s3Backup.s3Delete().delete(player, args[1]);
                    } else {
                        s3Backup.sendMessage(player, helpString);
                    }
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("sign")) {
                if (s3Backup.hasPermission(player, "s3backup.sign")) {
                    if (args.length == 2) {
                        s3Backup.s3Sign().sign(player, args[1]);
                    } else {
                        s3Backup.sendMessage(player, helpString);
                    }
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else if (args[0].equalsIgnoreCase("get")) {
                if (s3Backup.hasPermission(player, "s3backup.get")) {
                    if (args.length == 2) {
                        new S3Get(s3Backup, player, args[1]).runTaskAsynchronously(s3Backup);
                    } else {
                        s3Backup.sendMessage(player, helpString);
                    }
                } else {
                    s3Backup.sendMessage(player, "You do not have permission for this command");
                }
            } else {
                s3Backup.sendMessage(player, helpString);
            }
        } else {
            s3Backup.sendMessage(player, helpString);
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
        types.add("dry-run");

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

                TreeMap<Date, S3ObjectSummary> backupMap = new S3List(s3Backup).list(null, false, 0);
                ArrayList<String> backups = new ArrayList<>();

                for (S3ObjectSummary backup : backupMap.values()) {
                    backups.add(backup.getKey().replace(s3Backup.getFileConfig().getPrefix(), ""));
                }

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