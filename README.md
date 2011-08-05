## ABOUT

VRaptor uses XStream to serialize JSON from controllers, we don't like XStream because it serializes java.util.Map like this:

    {"map": [
      [
        "key",
        "value"
      ]
    ]}

Instead of:

    {"key":"value"}

I've tried to use git://github.com/luizsignorelli/vraptor-gson-serializer.git, but it doesn't serialize HypermediaResources' links. And, it uses an Annotation to set support.
So, I write this one.

## INSTALATION

    git clone git@github.com:acdesouza/vraptor-gson-plugin.git
    cd vraptor-gson-serializer
    mvn install

## CONFIGURATION

1. Add the dependency to your, maven, project

    <dependency>
      <groupId>br.com.caelum.vraptor-contrib</groupId>
    	<artifactId>vraptor-gson-plugin</artifactId>
    	<version>0.1.0</version>
    </dependency>

2. Add this to your web.xml:

    <!-- To enable it with Restfulie -->
    <context-param>
	  	<param-name>br.com.caelum.vraptor.packages</param-name>
	  	<param-value>br.com.caelum.vraptor.restfulie,br.com.beyondclick.vraptor</param-value>
	  </context-param>

Or

    <!-- To enable it with Restfulie -->
    <context-param>
	  	<param-name>br.com.caelum.vraptor.packages</param-name>
	  	<param-value>br.com.beyondclick.vraptor</param-value>
	  </context-param>

## USAGE

Nothing special about using it. Just plain old VRaptor way ;)


    @Resource @Path("/customer")
    public class CustomerController {

        private final Customers customers;
        private final Result result;

        public CustomerController(Customers customers, Result result) {
            this.customers = customers;
            this.result = result;
        }

        @Get("/{name}")
        public void findByName(String name){
            return result.use(representation()).from(Arrays.asList(customers.findByName(name))).recursive().serialize();
        }
    }

So you can access this resource, from terminal, using:

    curl -i 'http://localhost:8080/app/customer/antonio' -H 'Accept: application/json'

## CHANGES

### 0.1.0

This is the first version. So, no changes :)
