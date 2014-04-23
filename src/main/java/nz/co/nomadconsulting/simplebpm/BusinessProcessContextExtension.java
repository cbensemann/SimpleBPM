package nz.co.nomadconsulting.simplebpm;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class BusinessProcessContextExtension implements Extension {

    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event, BeanManager manager) {
      event.addScope(BusinessProcessScoped.class, true, true);
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
      event.addContext(new BusinessProcessScopeContext(manager));
    }

}
