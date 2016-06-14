package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.errors.NoPathParam;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargetMapper {

  private final List<String> namesForGroups;
  private final Pattern mappingPattern;
  private String targetMapping;
  private final MethodFilter methodFilter;

  public TargetMapper(String targetMapping, MethodFilter methodFilter) {
    this.targetMapping = targetMapping;
    this.methodFilter = methodFilter;
    //  EXAMPLE: targetMapping =  /asd/{clientId}/cool-phase/{phone}

    StringBuilder pattern = new StringBuilder();

    int pos = 0;

    List<String> namesForGroups = new ArrayList<>();

    while (true) {

      final int open = targetMapping.indexOf('{', pos);
      if (open < 0) {
        pattern.append(quote(targetMapping.substring(pos)));
        break;
      }

      final int close = targetMapping.indexOf('}', open);
      if (close < 0) {
        pattern.append(quote(targetMapping.substring(pos)));
        break;
      }

      pattern.append(quote(targetMapping.substring(pos, open)));

      String name = targetMapping.substring(open + 1, close).trim();
      if (name.endsWith("+")) {
        name = name.substring(0, name.length() - 1).trim();
        pattern.append("(.+)");
      } else {
        pattern.append("(.*)");
      }
      namesForGroups.add(name);

      pos = close + 1;

    }

    this.namesForGroups = Collections.unmodifiableList(namesForGroups);
    this.mappingPattern = Pattern.compile(pattern.toString());
  }

  public String infoStr() {
    StringBuilder sb = new StringBuilder();
    sb.append(targetMapping);
    if (methodFilter != null) {
      if (methodFilter.value().length == 0) {
        sb.append(", BLOCKED (empty method list)");
      } else {
        sb.append(", only for ");
        for (RequestMethod requestMethod : methodFilter.value()) {
          sb.append(requestMethod).append(", ");
        }
        sb.setLength(sb.length() - 2);
      }
    }
    return sb.toString();
  }

  private static StringBuilder quote(String str) {
    StringBuilder ret = new StringBuilder();
    for (int i = 0, C = str.length(); i < C; i++) {
      final char c = str.charAt(i);
      if (c == '-' || c == '\\' || c == '.' || c == '+' || c == '(' || c == ')' || c == '[' || c == ']') {
        ret.append('\\').append(c);
      } else if (c == '*') {
        ret.append(".*");
      } else {
        ret.append(c);
      }
    }
    return ret;
  }

  private static final MappingResult UNMAPPED_RESULT = new MappingResult() {
    @Override
    public boolean ok() {
      return false;
    }

    @Override
    public String getParam(String name) {
      throw new UnsupportedOperationException();
    }
  };

  public MappingResult mapTarget(RequestTunnel tunnel) {

    final Matcher matcher = mappingPattern.matcher(tunnel.getTarget());
    if (!matcher.matches()) return UNMAPPED_RESULT;

    if (!isMethodCorrect(tunnel.getRequestMethod())) return UNMAPPED_RESULT;

    Map<String, String> params = new HashMap<>();

    for (int i = 0, n = namesForGroups.size(); i < n; i++) {
      params.put(namesForGroups.get(i), matcher.group(i + 1));
    }

    final Map<String, String> unmodifiableParams = Collections.unmodifiableMap(params);

    return new MappingResult() {
      @Override
      public boolean ok() {
        return true;
      }

      @Override
      public String getParam(String name) {
        if (unmodifiableParams.containsKey(name)) return unmodifiableParams.get(name);
        throw new NoPathParam(name, unmodifiableParams);
      }
    };
  }

  private boolean isMethodCorrect(RequestMethod method) {
    if (methodFilter == null) return true;

    for (RequestMethod rm : methodFilter.value()) {
      if (rm == method) return true;
    }

    return false;
  }
}
