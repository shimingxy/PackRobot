package com.packrobot.firefly;

import com.hansky.apps.firefly.client.core.ResourceUtil;
import com.hansky.apps.firefly.intf.FireflyConstants;
import com.hansky.apps.firefly.intf.FireflyException;
import com.hansky.apps.firefly.intf.IResourceUtil;
import com.hansky.apps.firefly.intf.client.IArgument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class FireflyArguments
  implements IArgument, FireflyConstants
{
  IResourceUtil resUtil;
  boolean DEBUG = false;
  public static final String host = "-h";
  public static final String port = "-port";
  public static final String proj = "-proj";
  public static final String ws = "-b";
  public static final String repository = "-rep";
  public static final String comment = "-c";
  public static final String parent = "-p";
  public static final String branch = "-b";
  public static final String state = "-state";
  public static final String name = "-name";
  public static final String crid = "-crid";
  public static final String csid = "-csid";
  public static final String depth = "-depth";
  public static final String reviewer = "-r";
  public static final String time = "-t";
  public static final String st = "-st";
  public static final String label = "-l";
  public static final String silent = "-i";
  public static final String silentYes = "-yes";
  public static final String silentNo = "-no";
  public static final String preview = "-n";
  public static final String script = "-script";
  public static final String relative = "-ar";
  public static final String help = "-help";
  public static final String help1 = "/help";
  public static final String usage = "-?";
  public static final String usage1 = "/?";
  public static final String changeset = "-cs";
  public static final String desc = "-desc";
  public static final String root = "-root";
  public static final String inherit = "-ups";
  public static final String inheritLink = "-upl";
  public static final String ssl = "-ssl";
  public static final String checkcsincr = "-chknoncs";
  public static final String pl = "-pl";
  public static final String version = "-v";
  public static final String offline = "-off";
  public static final String all = "-all";
  public static final String allcr = "-allcr";
  public static final String printpending = "-printpending";
  public static final String nr = "-nr";
  public static final String nd = "-nd";
  public static final String multi = "multi";
  public static final String remains = "remains";
  public static final String fileType = "-ft";
  public static final String edit = "-e";
  public static final String dir = "-d";
  public static final String vh = "-vh";
  public static final String lock = "-lock";
  public static final String update = "-up";
  public static final String out = "-o";
  public static final String hist = "-hist";
  public static final String date = "-date";
  public static final String version1 = "-v1";
  public static final String version2 = "-v2";
  public static final String out1 = "-o1";
  public static final String out2 = "-o2";
  public static final String ignoreWhite = "ignoreWhite";
  public static final String ignoreCase = "ignoreCase";
  public static final String ignoreOS = "ignoreOS";
  public static final String option = "option";
  public static final String nolabel = "-nl";
  public static final String username = "-u";
  public static final String password = "-pwd";
  public static final String rusername = "-ru";
  public static final String rpassword = "-rpwd";
  public static final String branches = "-branches";
  public static final String brother = "-brother";
  public static final String labelOnly = "-label";
  public static final String user = "-user";
  public static final String quick = "-nq";
  public static final String label1 = "-l1";
  public static final String label2 = "-l2";
  public static final String bringover = "-bo";
  public static final String rebringover = "-rebr";
  public static final String reputback = "-repb";
  public static final String b1 = "-b1";
  public static final String b2 = "-b2";
  public static final String newlabelname = "-nln";
  public static final String checkrelatedcs = "-checkrcs";
  public static final String noContent = "-nc";
  public static final String full = "-full";
  public static final String prev = "-prev";
  public static final String incre = "-incre";
  public static final String force = "-f";
  public static final String checkInode = "-ckInode";
  public static final String metadata = "-meta";
  public static final String content = "-cont";
  public static final String cache = "-cache";
  public static final String mode = "-mode";
  public static final String startDate = "-d1";
  public static final String endDate = "-d2";
  public static final String host1 = "-h1";
  public static final String host2 = "-h2";
  public static final String port1 = "-p1";
  public static final String port2 = "-p2";
  public static final String proj1 = "-proj1";
  public static final String proj2 = "-proj2";
  public static final String ws1 = "-b1";
  public static final String ws2 = "-b2";
  public static final String replica = "-replica";
  public static final String indexdir = "-indexdir";
  public static final String deleted = "-deleted";
  public static final String keyword = "-kf";
  public static final String newline = "-nl";
  public static final String serverTime = "-s";
  public static final String trigger = "-trg";
  public static final String forceResolveLoactionConflict = "-frlc";
  public static final String noTrigger = "-notrg";
  public static final String autoResolveWithParent = "-arwp";
  public static final String autoResolveWithParentOutFile = "-arwpo";
  public static final String addUser = "-add";
  public static final String delUser = "-del";
  public static final String upward = "-upward";
  public static final String down = "-down";
  public static final String paths = "paths";
  public static final String checkout = "checkout";
  public static final String failed = "failed";
  public static final String deny = "deny";
  public static final String sccCommand = "scc";
  public static final String as400name = "-400name";
  public static final String as400user = "-400user";
  public static final String as400password = "-400pwd";
  public static final String as400Lib = "-400lib";
  public static final String to = "-to";
  public static final String verbose = "-verbose";
  public static final String filelist = "-file";
  public static final String filelist1 = "-f1";
  public static final String filelist2 = "-f2";
  public static final String labellist = "-lf";
  public static final String hijacked = "-hijacked";
  public static final String get = "-get";
  public static final String repair = "-repair";
  public static final String notStopMonitor = "-nsm";
  public static final String ext = "-ext";
  public static final String rbname = "-rbname";
  public static final String nofile = "-nf";
  public static final String level = "-level";
  public static final String noinactivelabel = "-nil";
  public static final String withChild = "-withchild";
  public static final String[] mkeys = { "-h", "-port", "-proj", "-b", "-c", "-p", "-b", "-state", "-crid", "-csid", "-depth", "-r", "-l", "-pl", "-v", "-t", "-name", "-d", "-ft", "-o", "-date", "-v1", "-v2", "-o1", "-o2", "-u", "-pwd", "-ru", "-rpwd", "-rep", "-incre", "-mode", "-branches", "-prev", "-h1", "-h2", "-p1", "-p2", "-proj1", "-proj2", "-b1", "-b2", "-b1", "-b2", "-l1", "-l2", "-nln", "-replica", "-indexdir", "-user", "-to", "-verbose", "-file", "-cs", "-desc", "-lf", "-f1", "-f2", "-rbname", "-level", "-d1", "-d2", "-arwpo" };

  public static final String[] bkeys = { "-st", "-off", "-all", "-allcr", "-printpending", "-i", "-yes", "-no", "-vh", "-lock", "-nr", "-help", "-?", "-e", "-hist", "-nq", "-full", "-checkrcs", "-nc", "-n", "-bo", "-meta", "-cont", "-cache", "-f", "-ckInode", "-brother", "-root", "-ups", "-nl", "-nd", "/help", "/?", "-label", "-deleted", "-kf", "-nl", "-s", "-up", "-ar", "-script", "-hijacked", "-ssl", "-get", "-repair", "-nsm", "-ext", "-trg", "-notrg", "-upl", "-nf", "-withchild", "-nil", "-arwp", "-frlc", "-add", "-del", "-upward", "-down", "-chknoncs" };

  public static final String[] rkeys = { "remains" };
  String[] argv;
  int argc;
  Hashtable hash = new Hashtable();
  String[] remain;
  String[] first;
  String err;

  public FireflyArguments()
  {
    this.resUtil = ResourceUtil.getResourceUtil(2);
  }

  public FireflyArguments(String[] argv) throws FireflyException {
    this.resUtil = ResourceUtil.getResourceUtil(2);
    if (argv != null) {
      this.argv = argv;
      this.argc = argv.length;
      buildArguments();
      buildRemains();
    }
  }

  void buildRemains() throws FireflyException {
    String[] remain0 = getRemainFromFile();
    if (remain0 != null)
      if (this.remain == null) {
        this.remain = remain0;
      } else {
        int len = this.remain.length;
        String[] nremain = new String[this.remain.length + remain0.length];
        System.arraycopy(this.remain, 0, nremain, 0, this.remain.length);
        System.arraycopy(remain0, 0, nremain, len, remain0.length);
        this.remain = nremain;
      }
  }

  public FireflyArguments(int off, String[] argv) throws FireflyException
  {
    this.resUtil = ResourceUtil.getResourceUtil(2);
    if ((argv != null) && 
      (argv.length > off)) {
      this.argc = (argv.length - off);
      this.argv = new String[this.argc];
      System.arraycopy(argv, off, this.argv, 0, this.argc);
      buildArguments();
      buildRemains();
    }
  }

  public FireflyArguments(IArgument arg)
  {
    this.resUtil = ResourceUtil.getResourceUtil(2);
    if (arg != null) {
      this.hash = ((FireflyArguments)arg).hash;
      this.remain = ((FireflyArguments)arg).remain;
    }
  }

  public FireflyArguments(Hashtable hash, String[] remain) {
    this.resUtil = ResourceUtil.getResourceUtil();
    this.hash = hash;
    this.remain = remain;
  }

  void buildArguments() throws FireflyException {
    if (this.argv == null) {
      return;
    }
    boolean isFirst = true;
    for (int i = 0; i < this.argc; i++) {
      StringBuffer str = new StringBuffer();
      int cmdIdx = getCmd(i);
      if (cmdIdx != -1) {
        i = cmdIdx;
      }

      if (isFirst) {
        isFirst = false;
        if (cmdIdx > 0) {
          this.first = new String[cmdIdx];
          System.arraycopy(this.argv, 0, this.first, 0, cmdIdx);
        }
      }

      if (cmdIdx == -1) {
        this.remain = new String[this.argc - i];
        System.arraycopy(this.argv, i, this.remain, 0, this.argc - i);
        return;
      }

      int nextCmdIdx = getCmd(cmdIdx + 1);

      if (nextCmdIdx == -1) {
        int fl = this.first == null ? 0 : this.first.length;
        int rl = this.argc - cmdIdx - 2;
        if (this.first != null) {
          this.remain = this.first;
        } else if (rl >= 0) {
          this.remain = new String[rl];
          System.arraycopy(this.argv, cmdIdx + 2, this.remain, 0, rl);
        }
      }

      if (inKeys(cmdIdx)) {
        if (nextCmdIdx == -1) {
          if ((cmdIdx + 1 < this.argv.length) && (this.argv[(cmdIdx + 1)] != null)) {
            if (this.first == null) {
              if (this.hash.contains(this.argv[cmdIdx])) {
                error(cmdIdx);
              }
              putArgument(this.argv[cmdIdx], this.argv[(cmdIdx + 1)]);
              return;
            }
            nextCmdIdx = this.argc;
          }
          else {
            error(cmdIdx);
          }
        }
        if (cmdIdx == nextCmdIdx - 1) {
          error(cmdIdx);
        }
        for (int j = cmdIdx + 1; j < nextCmdIdx; j++) {
          i++;
          if (this.argv[j] != null) {
            str.append(this.argv[j].trim());
            if (j != nextCmdIdx - 1) {
              str.append(" ");
            }
          }
        }
        if (str.length() == 0) {
          error(cmdIdx);
        }
        if (this.hash.contains(this.argv[cmdIdx])) {
          error(cmdIdx);
        }
        putArgument(this.argv[cmdIdx], str.toString());
      } else if (inBKeys(cmdIdx)) {
        if (this.hash.contains(this.argv[cmdIdx])) {
          error(cmdIdx);
        }
        putArgument(this.argv[cmdIdx], "");
      }
    }
  }

  void putArgument(String key, String value) throws FireflyException {
    if ((key != null) && (value != null)) {
      this.hash.put(key, value);
      if ((key.startsWith("-i")) && (key.length() > 2) && (key.length() < 6)) {
        String suffix = key.substring(2);
        boolean iwhite = false;
        boolean ios = false;
        boolean icase = false;
        boolean valid = true;
        for (int i = 0; i < suffix.length(); i++) {
          char c = suffix.charAt(i);
          if (c == 'w') {
            iwhite = true;
          } else if (c == 'o') {
            ios = true;
          } else if (c == 'c') {
            icase = true;
          } else {
            valid = false;
            break;
          }
        }
        if (valid) {
          if (iwhite) {
            this.hash.put("ignoreWhite", "");
          }
          if (ios) {
            this.hash.put("ignoreOS", "");
          }
          if (icase) {
            this.hash.put("ignoreCase", "");
          }
        }
      }
    }
    if ("-port".equals(key)) {
      int pv = -1;
      try {
        pv = Integer.parseInt(value);
      } catch (NumberFormatException nfe) {
        throw new FireflyException("Invalid port number: " + value);
      }
      if ((pv < 1024) || (pv > 65535))
        throw new FireflyException("Invalid port number: " + value);
    }
  }

  public void setArgument(String option, Object value)
  {
    if ("remains".equals(option)) {
      if (value == null)
        this.remain = null;
      else {
        this.remain = ((String[])value);
      }
      return;
    }
    if (value != null) {
      if (option != null) {
        this.hash.put(option, value);
      }
    }
    else if (option != null)
      this.hash.remove(option);
  }

  public Object getArgument(String key)
  {
    return this.hash.get(key);
  }

  int getCmd(int off) {
    for (int i = off; i < this.argc; i++) {
      String cmd = this.argv[i];
      if ((inKeys(cmd) | inBKeys(cmd))) {
        return i;
      }
    }
    return -1;
  }

  boolean inKeys(int cmdIdx) {
    return inKeys(this.argv[cmdIdx]);
  }

  boolean inBKeys(int cmdIdx) {
    return inBKeys(this.argv[cmdIdx]);
  }

  boolean inKeys(String cmd) {
    if (cmd == null) {
      return false;
    }
    for (int i = 0; i < mkeys.length; i++) {
      if (cmd.equals(mkeys[i])) {
        return true;
      }
    }
    return false;
  }

  boolean inBKeys(String cmd) {
    if (cmd == null) {
      return false;
    }
    for (int i = 0; i < bkeys.length; i++) {
      if (cmd.equals(bkeys[i])) {
        return true;
      }
      if ((cmd.startsWith("-i")) && (cmd.length() > 2) && (cmd.length() < 6)) {
        String suffix = cmd.substring(2);
        return checkDiffArguments(suffix);
      }
    }
    return false;
  }

  boolean checkDiffArguments(String s) {
    if (s == null) {
      return false;
    }

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c != 'w') && (c != 'o') && (c != 'c'))
      {
        return false;
      }
    }
    return true;
  }

  boolean inRKeys(String option) {
    if (option == null) {
      return false;
    }
    for (int i = 0; i < rkeys.length; i++) {
      if (option.equals(rkeys[i])) {
        return true;
      }
    }
    return false;
  }

  public String getString(String option)
  {
    return (String)this.hash.get(option);
  }

  public String[] getRemains()
  {
    return this.remain;
  }

  String[] getRemainFromFile() throws FireflyException {
    String file = (String)getArgument("-file");
    if (file != null) {
      File f = new File(file);
      if (!f.isAbsolute()) {
        f = new File(System.getProperty("user.dir"), file);
      }
      if (!f.exists())
        throw new FireflyException(65935, new String[] { f.getPath() });
      try
      {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        try {
          Vector v = new Vector();
          while (true) {
            String line = br.readLine();
            if (line == null)
            {
              break;
            }

            if ("".equals(line.trim()))
            {
              continue;
            }
            v.addElement(line);
          }
          String[] remain0 = new String[v.size()];
          v.copyInto(remain0);
          String[] arrayOfString1 = remain0;
          return arrayOfString1; } finally { br.close(); }
      }
      catch (IOException ie) {
      }
    }
    return null;
  }

  public int getInt(String value) throws FireflyException
  {
    try {
      return Integer.parseInt(value);
    }
    catch (Exception err) {
    }throw new FireflyException(65536, this.resUtil.getString("invalid.arguments", value));
  }

  public float getFloat(String value)
  {
    throw new RuntimeException(this.resUtil.getString("implements.later"));
  }

  public double getDouble(String value)
  {
    throw new RuntimeException(this.resUtil.getString("implements.later"));
  }

  public long getLong(String value) throws FireflyException
  {
    try {
      return Long.parseLong(value);
    }
    catch (NumberFormatException err) {
      //err = this.resUtil.getString("invalid.arguments", value);
    	throw new FireflyException(65536, err);
    }
  }

  public boolean getBoolean(String option)
  {
    return this.hash.get(option) != null;
  }

  public String getText(String option)
  {
    return (String)this.hash.get(option);
  }

  void error(int cmdIdx) throws FireflyException {
    String err = this.resUtil.getString("invalid.arguments", this.argv[cmdIdx]);
    throw new FireflyException(65536, err);
  }

  void error(String option) throws FireflyException {
    String err = this.resUtil.getString("invalid.arguments", option);
    throw new FireflyException(65536, err);
  }

  void status(String message) {
    System.out.println(message);
  }

  @Override
public String toString() {
	return "FireflyArguments [resUtil=" + resUtil + ", DEBUG=" + DEBUG + ", argv=" + Arrays.toString(argv) + ", argc="
			+ argc + ", hash=" + hash + ", remain=" + Arrays.toString(remain) + ", first=" + Arrays.toString(first)
			+ ", err=" + err + "]";
}

public static void main(String[] args) throws Exception {
	FireflyArguments arg = new FireflyArguments(args);
    Enumeration keys = arg.hash.keys();
    while (keys.hasMoreElements()) {
      String key = (String)keys.nextElement();
      if (key != null) {
        String value = (String)arg.hash.get(key);
        System.out.println("key : " + key);
        System.out.println("value : " + value);
      }
    }
    if (arg.remain != null) {
      System.out.println("remain is:");
      for (int i = 0; i < arg.remain.length; i++)
        System.out.println(arg.remain[i]);
    }
  }
}
