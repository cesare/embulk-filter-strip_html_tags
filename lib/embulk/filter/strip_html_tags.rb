Embulk::JavaPlugin.register_filter(
  "strip_html_tags", "org.embulk.filter.strip_html_tags.StripHtmlTagsFilterPlugin",
  File.expand_path('../../../../classpath', __FILE__))
