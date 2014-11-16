package com.mycompany.sonar.reference.ui;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;

@UserRole(UserRole.USER)
@Description("Show how to use Ruby Widget API")
@WidgetCategory("Sample")
@WidgetProperties({
  @WidgetProperty(key = "param1",
    description = "This is a mandatory parameter",
    optional = false
  ),
  @WidgetProperty(key = "max",
    description = "max threshold",
    type = WidgetPropertyType.INTEGER,
    defaultValue = "80"
  ),
  @WidgetProperty(key = "param2",
    description = "This is an optional parameter"
  ),
  @WidgetProperty(key = "floatprop",
    description = "test description"
  )
})
public class ExampleRubyWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "sample";
  }

  public String getTitle() {
    return "Sample";
  }

  @Override
  protected String getTemplatePath() {
    return "/example/example_widget.html.erb";
  }
}
