require "nokogiri"

module Embulk
  module Filter

    class StripHtmlTags < FilterPlugin
      Plugin.register_filter("strip_html_tags", self)

      def self.transaction(config, in_schema, &control)
        task = {
          "columns" => config.param("columns", :array, default: []),
        }

        out_columns = in_schema

        yield(task, out_columns)
      end

      attr_reader :target_columns, :target_fields

      def init
        @target_columns = task["columns"]
        @target_fields = out_schema.map {|c| @target_columns.include?(c.name) }
      end

      def close
      end

      def add(page)
        page.each do |record|
          page_builder.add(fix_record(record))
        end
      end

      def finish
        page_builder.finish
      end

      private

      def fix_record(record)
        record.zip(target_fields).map do |(value, target)|
          if target
            strip_tags(value)
          else
            value
          end
        end
      end

      def strip_tags(str)
        Nokogiri::HTML.parse(str).text
      end
    end
  end
end
