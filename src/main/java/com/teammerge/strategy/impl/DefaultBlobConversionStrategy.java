package com.teammerge.strategy.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.teammerge.IStoredSettings;
import com.teammerge.Keys;
import com.teammerge.strategy.BlobConversionStrategy;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

public class DefaultBlobConversionStrategy implements BlobConversionStrategy {

  @Override
  public Map<String, Object> convert(String blobPath, Repository r, RevCommit commit,
      IStoredSettings settings) {
    Map<String, Object> res = new HashMap<>();
    String extension = null;

    String[] encodings = getEncodings(settings);

    if (blobPath.lastIndexOf('.') > -1) {
      extension = blobPath.substring(blobPath.lastIndexOf('.') + 1).toLowerCase();
    }

    // Map the extensions to types
    Map<String, Integer> map = new HashMap<String, Integer>();
    for (String ext : settings.getStrings(Keys.web.prettyPrintExtensions)) {
      map.put(ext.toLowerCase(), 1);
    }
    for (String ext : settings.getStrings(Keys.web.imageExtensions)) {
      map.put(ext.toLowerCase(), 2);
    }
    for (String ext : settings.getStrings(Keys.web.binaryExtensions)) {
      map.put(ext.toLowerCase(), 3);
    }
    if (extension != null) {
      int type = 0;
      if (map.containsKey(extension)) {
        type = map.get(extension);
      }
      switch (type) {
        case 2:
          // image blobs
          // TODO
          break;
        case 3:
          // binary blobs
          // TODO
          break;
        default:
          // plain text
          String source = JGitUtils.getStringContent(r, commit.getTree(), blobPath, encodings);
          String table;
          if (source == null) {
            table = missingBlob(blobPath, commit);
          } else {
            table = generateSourceView(source, extension, type == 1, settings);
            // addBottomScriptInline("jQuery(prettyPrint);");
          }
          res.put( BlobConversionStrategy.Key.SOURCE.name(), table);
          res.put(BlobConversionStrategy.Key.FILE_EXTENSION.name(), extension);
      }
    } else {
      // plain text
      String source = JGitUtils.getStringContent(r, commit.getTree(), blobPath, encodings);
      String table;
      if (source == null) {
        table = missingBlob(blobPath, commit);
      } else {
        table = generateSourceView(source, null, false, settings);
        // addBottomScriptInline("jQuery(prettyPrint);");
      }
      res.put( BlobConversionStrategy.Key.SOURCE.name(), table);
    }

    return res;
  }

  protected String missingBlob(String blobPath, RevCommit commit) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"alert alert-error\">");
    String pattern =
        "{0} does not exist in tree {1}".replace("{0}", "<b>{0}</b>").replace("{1}", "<b>{1}</b>");
    sb.append(MessageFormat.format(pattern, blobPath, commit.getTree().getId().getName()));
    sb.append("</div>");
    return sb.toString();
  }

  protected String generateSourceView(String source, String extension, boolean prettyPrint,
      IStoredSettings settings) {
    String[] lines = source.split("\n");

    StringBuilder sb = new StringBuilder();
    sb.append("<!-- start blob table -->");
    sb.append("<table width=\"100%\"><tbody><tr>");

    // nums column
    sb.append("<!-- start nums column -->");
    sb.append("<td id=\"nums\">");
    sb.append("<pre>");
    String numPattern = "<span id=\"L{0}\" class=\"jump\"></span><a href=\"#L{0}\">{0}</a>\n";
    for (int i = 0; i < lines.length; i++) {
      sb.append(MessageFormat.format(numPattern, "" + (i + 1)));
    }
    sb.append("</pre>");
    sb.append("<!-- end nums column -->");
    sb.append("</td>");

    sb.append("<!-- start lines column -->");
    sb.append("<td id=\"lines\">");
    sb.append("<div class=\"sourceview\">");
    if (prettyPrint) {
      sb.append("<pre class=\"prettyprint lang-" + extension + "\">");
    } else {
      sb.append("<pre class=\"plainprint\">");
    }
    final int tabLength = settings.getInteger(Keys.web.tabLength, 4);
    lines = StringUtils.escapeForHtml(source, true, tabLength).split("\n");

    sb.append("<table width=\"100%\"><tbody>");

    String linePattern = "<tr class=\"{0}\"><td><div><span class=\"line\">{1}</span></div>\r</tr>";
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i].replace('\r', ' ');
      String cssClass = (i % 2 == 0) ? "even" : "odd";
      if (StringUtils.isEmpty(line.trim())) {
        line = "&nbsp;";
      }
      sb.append(MessageFormat.format(linePattern, cssClass, line, "" + (i + 1)));
    }
    sb.append("</tbody></table></pre>");
    sb.append("</pre>");
    sb.append("</div>");
    sb.append("</td>");
    sb.append("<!-- end lines column -->");

    sb.append("</tr></tbody></table>");
    sb.append("<!-- end blob table -->");

    return sb.toString();
  }

  protected String[] getEncodings(IStoredSettings settings) {
    return settings.getStrings(Keys.web.blobEncodings).toArray(new String[0]);
  }


}
