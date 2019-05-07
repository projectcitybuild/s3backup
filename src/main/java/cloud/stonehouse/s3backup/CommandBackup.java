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

class CommandBackup implements TabExecutor {

    private final S3Backup s3Backup;

    CommandBackup(S3Backup s3Backup) {
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
                    ArrayList<String> backups = s3Backup.getS3List().list();
                    if (backups.size() == 0) {
                        s3Backup.sendMessage(player, true, "There are no backups to list.");
                    } else {
                        for (String backup : backups) {
                            s3Backup.sendMessage(player, true, backup);
                        }
                    }
                } catch (Exception e) {
                    s3Backup.sendMessage(player, false, "Error retrieving backup: " + e.getLocalizedMessage());
                    s3Backup.exception(e);
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    s3Backup.getS3Delete().delete(player, args[1]);
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
                    Collections.sort(result);
                    return result;
                } else {
                    Collections.sort(types);
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
                        Collections.sort(result);
                        return result;
                    } else {
                        Collections.sort(backups);
                        return backups;
                    }
                }
            }
        }
        return null;
    }
}