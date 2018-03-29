package dg.athena.sideprojects.SlotService;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import dg.athena.sideprojects.SlotService.Domain.DomainEvent;
import dg.athena.sideprojects.SlotService.Domain.Slot;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

@Service
public class Repository{
    public static final String SLOT_EVENTS = "slot-events";
    public static final String SNAPSHOTS_FOR_SLOTS = "slots";
    
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    private final KStreamBuilderFactoryBean kStreamBuilderFactoryBean;

    private ReadOnlyKeyValueStore<String,Slot> store;

    private ReadOnlyKeyValueStore<String,Slot> getStore(){
        if(this.store == null){
            this.store = kStreamBuilderFactoryBean
            .getKafkaStreams()
            .store(Repository.SNAPSHOTS_FOR_SLOTS, keyValueStore());
        }
        return this.store;
    }

    @Autowired
    public Repository(KafkaTemplate<String, DomainEvent> kafkaTemplate, KStreamBuilderFactoryBean kStreamBuilderFactoryBean) {
        this.kafkaTemplate = kafkaTemplate;
        this.kStreamBuilderFactoryBean = kStreamBuilderFactoryBean;
    }

    public void save(Slot slot) {
        List<DomainEvent> newEvents = slot.getDirtyEvents();
        newEvents.forEach(
            domainEvent -> kafkaTemplate.send(SLOT_EVENTS, domainEvent.aggregateUUID().toString(),domainEvent) 
        );
        slot.flushEvents();
    }

    public List<Slot> findAll() {
        List<Slot> slots = new ArrayList<>();
        this.getStore().all()
            .forEachRemaining(
                stringSlotKeyValue -> slots.add(stringSlotKeyValue.value)
            );
        // ReadOnlyKeyValueStore<String,Slot> store = kStreamBuilderFactoryBean
        // .getKafkaStreams()
        // .store(Repository.SNAPSHOTS_FOR_SLOTS, keyValueStore());
        // store.all()
        //     .forEachRemaining(
        //             stringSlotKeyValue -> slots.add(stringSlotKeyValue.value)
        //     );
        return  slots;
    }

    public Slot findById(String uuid){
        return this.getStore().get(uuid);
    }
}