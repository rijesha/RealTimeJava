import fr.dgac.ivy.*;
 
class IvyHandler {
  private Ivy bus;
  
  IvyHandler(String name, IvyMessageListener ivyMessageListener, String regexString) throws IvyException {
    bus = new Ivy(name, name + " Ready",null);
    // classical subscription
    bus.bindMsg(regexString, ivyMessageListener);
    // inner class subscription ( think awt )
    bus.bindMsg("^Bye$",new IvyMessageListener() {
      public void receive(IvyClient client, String[] args) {
 // leaves the bus, and as it is the only thread, quits
        bus.stop();
      }
    });
    bus.start(null); // starts the bus on the default domain
  }
  
    public void send(String data) {
    try {
      bus.sendMsg(data);
    } catch (IvyException ie) {
      System.out.println("can't send my message on the bus");
    }
  }

}
