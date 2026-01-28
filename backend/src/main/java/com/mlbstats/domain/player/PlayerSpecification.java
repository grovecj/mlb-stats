package com.mlbstats.domain.player;

import org.springframework.data.jpa.domain.Specification;

public class PlayerSpecification {

    public static Specification<Player> withCriteria(PlayerSearchCriteria criteria) {
        return Specification
                .where(withSearch(criteria.search()))
                .and(withPosition(criteria.position()))
                .and(withPositionType(criteria.positionType()))
                .and(withBats(criteria.bats()))
                .and(withThrows(criteria.throwsHand()))
                .and(withActive(criteria.active()));
    }

    private static Specification<Player> withSearch(String search) {
        if (search == null || search.isBlank()) {
            return (root, query, cb) -> null;
        }
        String searchPattern = "%" + search.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("fullName")), searchPattern);
    }

    private static Specification<Player> withPosition(String position) {
        if (position == null || position.isBlank()) {
            return (root, query, cb) -> null;
        }
        return (root, query, cb) -> cb.equal(root.get("position"), position);
    }

    private static Specification<Player> withPositionType(String positionType) {
        if (positionType == null || positionType.isBlank()) {
            return (root, query, cb) -> null;
        }
        return (root, query, cb) -> cb.equal(root.get("positionType"), positionType);
    }

    private static Specification<Player> withBats(String bats) {
        if (bats == null || bats.isBlank()) {
            return (root, query, cb) -> null;
        }
        return (root, query, cb) -> cb.equal(root.get("bats"), bats);
    }

    private static Specification<Player> withThrows(String throwsHand) {
        if (throwsHand == null || throwsHand.isBlank()) {
            return (root, query, cb) -> null;
        }
        return (root, query, cb) -> cb.equal(root.get("throwsHand"), throwsHand);
    }

    private static Specification<Player> withActive(Boolean active) {
        if (active == null) {
            return (root, query, cb) -> null;
        }
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
}
