package io.nkdtrdr.mrktmkr.persistence.processors;

import io.nkdtrdr.mrktmkr.persistence.model.Asset;
import org.springframework.data.repository.CrudRepository;


public interface AssetRepository extends CrudRepository<Asset, Long> {
}
