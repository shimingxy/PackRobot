package com.hansky.apps.firefly.client.cmdline.cmd;

import com.hansky.apps.firefly.client.cmdline.GenericCommand;
import com.hansky.apps.firefly.client.cmdline.cmd.BringoverListener;
import com.hansky.apps.firefly.client.cmdline.cmd.Login;
import com.hansky.apps.firefly.client.cmdline.cmd.Resolve;
import com.hansky.apps.firefly.client.core.ClientUtil;
import com.hansky.apps.firefly.client.core.LocalWorkspace;
import com.hansky.apps.firefly.intf.FireflyException;
import com.hansky.apps.firefly.intf.IProject;
import com.hansky.apps.firefly.intf.IWorkspace;
import com.hansky.apps.firefly.intf.client.IArgument;
import com.hansky.apps.firefly.intf.client.ILocalWorkspace;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FireflyBringover extends GenericCommand
{
  static boolean DEBUG = false;
  public static boolean isLocal;
  static IWorkspace parent;
  static IWorkspace child;
  boolean localCmd;
  static boolean checkHijacked = false;

  public FireflyBringover(boolean localCmd)
  {
    this.localCmd = localCmd;
  }

  public void usage(boolean detail) {
    if (!detail) {
      status(resUtil.getString(""));
      if (this.localCmd) {
        status(resUtil.getString("usage.hff.bringover.i.n.c.comments.paths"));
        status(resUtil.getString("type.hff.bringover..for.more.detail"));
      } else {
        status(resUtil.getString("usage.hff.branch.bringover.h.host.port.port.proj.project.b.branch.i.n.c.comments.paths"));
        status(resUtil.getString("type.hff.branch.bringover..for.more.detail"));
      }
      return;
    }
    status(resUtil.getString(""));
    if (this.localCmd)
      status(resUtil.getString("usage.hff.bringover.i.n.c.comments.paths"));
    else {
      status(resUtil.getString("usage.hff.branch.bringover.h.host.port.port.proj.project.b.branch.i.n.c.comments.paths"));
    }
    status(resUtil.getString(""));
    status(resUtil.getString("where.options.include"));
    if (!this.localCmd) {
      status(resUtil.getString("h.server.address"));
      status(resUtil.getString("port.server.port"));
      status(resUtil.getString("proj.project.name"));
      status(resUtil.getString("b.branch.name"));
    }
    status(resUtil.getString("i.ignore.all.questions"));
    status(resUtil.getString("n.preview.the.result"));
    status(resUtil.getString("c.some.comments"));
    status(resUtil.getString("file.filelist"));
    status(resUtil.getString("script"));
    status(resUtil.getString("paths.the.files.or.directories.you.want.to.bringover"));
    status(resUtil.getString(".or.help.print.the.usage.page"));
    status(resUtil.getString("cmdline.bringover.sample"));
    status(resUtil.getString(""));
  }

  public void runCmd(IArgument arg) throws FireflyException {
    if (!this.localCmd)
      remoteCmdInit(arg, null);
    else {
      localCmdInit(arg);
    }

    if (isOffline()) {
      error(65824, resUtil.getString("cant.perform.bringover.under.offline.mode"));
    }

    checkHijacked = "true".equals(this.state.getGlobalProps().getProperty("check.hijack"));
    try
    {
      bringover(this.state, arg);
    } catch (IOException e) {
      throw new FireflyException(e);
    }
  }

  protected boolean indexChanged() {
    return true;
  }

  public static void clearEnv() {
    isLocal = false;
    parent = null;
    child = null;
    checkHijacked = false;
  }

  public static boolean bringover(ILocalWorkspace state, IArgument arg) throws IOException, FireflyException {
    silent = arg.getBoolean("-i");
    script = arg.getBoolean("-script");
    boolean preview = arg.getBoolean("-n");
    String project = arg.getString("-proj");
    String pws = arg.getString("-p");
    String cws = arg.getString("-b");
    arg.setArgument("checkHijacked", checkHijacked ? "true" : "false");
    isLocal = state.isLocal();

    IProject proj = null;

    if (!isLocal) {
      Login.login(state, arg);
      proj = getProject(state, project);

      if (proj == null) {
        error(resUtil.getString("project.not.found"));
      }

      arg.setArgument("-proj", proj.getName());

      child = getWorkspace(state, proj, cws, resUtil.getString("child.branch.name.ask"));
      if (child == null) {
        error(resUtil.getString("please.input.child.workspace"));
      }
    }

    IWorkspace localws = null;
    if (state.isLocal()) {
      String cproject = state.getProperty("hansky.firefly.project.id");
      String cmws = state.getProperty("hansky.firefly.mirrorws.id");
      state.connect(null, -1, null, null);
      proj = getProject(state, cproject);
      localws = ClientUtil.getWorkspace(state, cproject, cmws);
    }

    if (proj == null) {
      proj = getProject(state, project);
    }

    state.setProperty("hansky.firefly.project.id", proj.getID().toStr().toString());

    if (!isLocal) {
      if (pws != null) {
        parent = getWorkspace(state, proj, pws, resUtil.getString("parent.branch.name.ask"));
      }
      else if (child != null) {
        parent = child.getParent();
      }

      if (parent == null) {
        error(65844, resUtil.getString("parent.branch.not.found"));
      }

      if (child == null) {
        error(65845, resUtil.getString("child.branch.not.found"));
      }
     
      arg.setArgument("-p", parent.getName());
      arg.setArgument("-b", child.getName());

      int ptype = parent.getWsType();
      int ctype = child.getWsType();

      if (ctype != 1) {
        error(65829, resUtil.getString("branch.bringover.target.can.only.be.server.branch"));
      }

      if (ptype == 2) {
        error(65830, resUtil.getString("cant.bringover.from.mirror"));
      }

      state.setProperty("hansky.firefly.parentws.id", parent.getRoot().toHexString());
    } else {
      child = localws;
      parent = child.getParent();
    }

    if ((state.isLocal()) && (child != null)) {
      ((LocalWorkspace)state).unlink(child);
    }
    
    System.out.println("parent "+parent.getName());
    System.out.println("child "+child.getName());
    System.out.println("isLocal "+isLocal);
    System.out.println("arg "+arg.toString());
    while (true)
    {
    BringoverListener bringover = new BringoverListener(state, arg, parent, child, isLocal);

      bringover.waitUntilDone();

      if (bringover.needReBringover) {
        arg.setArgument("remains", bringover.reBringoverLocs);
        arg.setArgument("-rebr", "");
        continue;
      }

      if (bringover.failed) {
        throw new FireflyException(1010019);
      }

      if (bringover.complete) {
        status(resUtil.getString("bringover.complete"));
        return true;
      }

      if (!bringover.allowed) {
        status(resUtil.getString("operation.denied"));
        throw new FireflyException(1010019);
      }

      if (bringover.editList != null) {
        status(resUtil.getString("some.files.involed.in.bringover.is.being.editing.aborted"));
        throw new FireflyException(1010019);
      }

      if (bringover.hijackedList != null) {
        status(resUtil.getString("some.files.involed.in.bringover.is.hijacked.file.aborted"));
        throw new FireflyException(1010019);
      }

      if (preview) {
        return false;
      }

      boolean autoResolveWithParent = arg.getBoolean("-arwp");
      if (bringover.conflictList != null) {
        int conflictCount = bringover.conflictList.size() / 5;
        if ((!autoResolveWithParent) && (silent)) {
          status(resUtil.getString("conflicts.remains.aborted"));
          throw new FireflyException(1010019);
        }

        if ((autoResolveWithParent) || (promptUser(resUtil.getString("do.you.want.to.resolve.the.conflicts"), true))) {
          Resolve.isLocal = isLocal;
          if (!Resolve.resolve(state, arg)) {
            status(resUtil.getString("conflicts.remains.aborted"));
            throw new FireflyException(1010019);
          }

          if (autoResolveWithParent) {
            String[] args0 = { "" + conflictCount };
            status(resUtil.getString("auto.resolve.with.parent.version.conflicts", args0));

            bringover.conflictInfo.append(resUtil.getString("auto.resolve.with.parent.version.conflicts", args0));
            printConflictInfo(bringover.conflictInfo, arg);
          }
        }
        else {
          throw new FireflyException(1010019);
        }
      }
    }
  }

  public static void printConflictInfo(StringBuffer content, IArgument arg)
  {
    String output = arg.getString("-arwpo");
    File fout = null;

    fout = new File(output);
    if (!fout.exists()) {
      fout.mkdirs();
    }
    if (fout.isDirectory()) {
      String nFile = fout.getName() + ".txt";
      fout = new File(fout, nFile);
    }

    InputStream fin = null;
    FileOutputStream foutput = null;
    try {
      fin = new ByteArrayInputStream(content.toString().getBytes());
      foutput = new FileOutputStream(fout);
      ClientUtil.copyStream(fin, foutput);
    } catch (IOException ie) {
      ie.printStackTrace();
    } finally {
      try {
        foutput.close();
        fin.close();
      } catch (IOException ie) {
        ie.printStackTrace();
      }
    }
  }
}